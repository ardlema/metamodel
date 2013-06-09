/**
 * eobjects.org MetaModel
 * Copyright (C) 2010 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.metamodel.dialects;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eobjects.metamodel.jdbc.dialects.DB2QueryRewriter;
import org.eobjects.metamodel.query.FunctionType;
import org.eobjects.metamodel.query.OperatorType;
import org.eobjects.metamodel.query.Query;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.MutableColumn;
import org.eobjects.metamodel.schema.MutableSchema;
import org.eobjects.metamodel.schema.MutableTable;
import org.eobjects.metamodel.schema.TableType;

public class DB2QueryRewriterTest extends TestCase {

    private MutableSchema schema;
    private MutableTable table;
    private MutableColumn col;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        schema = new MutableSchema("sch");
        table = new MutableTable("foo").setSchema(schema);
        schema.addTable(table);
        col = new MutableColumn("bar").setTable(table);
        table.addColumn(col);
    }

    public void testRewriteMaxRowsNoFirstRow() throws Exception {
        Query q = new Query().from(table).select(col).setMaxRows(400);
        String str = new DB2QueryRewriter(null).rewriteQuery(q);
        assertEquals("SELECT sch.foo.bar FROM sch.foo FETCH FIRST 400 ROWS ONLY", str);
    }
    
    public void testRewriteMaxRowsFirstRowIsOne() throws Exception {
        Query q = new Query().from(table).select(col).setMaxRows(200).setFirstRow(1);
        String str = new DB2QueryRewriter(null).rewriteQuery(q);
        assertEquals("SELECT sch.foo.bar FROM sch.foo FETCH FIRST 200 ROWS ONLY", str);
    }

    public void testRewriteFirstRow() throws Exception {
        Query q = new Query().from(table).select(col).setFirstRow(401);
        String str = new DB2QueryRewriter(null).rewriteQuery(q);

        assertEquals(
                "SELECT metamodel_subquery.bar FROM (SELECT sch.foo.bar, ROW_NUMBER() OVER() AS metamodel_row_number FROM sch.foo) metamodel_subquery WHERE metamodel_row_number > 400",
                str);
    }

    public void testRewriteFirstRowAndMaxRows() throws Exception {
        Query q = new Query().from(table).select(col).setFirstRow(401).setMaxRows(400);
        String str = new DB2QueryRewriter(null).rewriteQuery(q);
        assertEquals(
                "SELECT metamodel_subquery.bar FROM (SELECT sch.foo.bar, ROW_NUMBER() OVER() AS metamodel_row_number FROM sch.foo) metamodel_subquery WHERE metamodel_row_number BETWEEN 401 AND 800",
                str);
    }

    public void testRewriteColumnType() throws Exception {
        assertEquals("SMALLINT", new DB2QueryRewriter(null).rewriteColumnType(ColumnType.BOOLEAN));

        assertEquals("VARCHAR", new DB2QueryRewriter(null).rewriteColumnType(ColumnType.VARCHAR));
    }

    public void testRewriteSelectItems() throws Exception {

        Query q = new Query().from(table).select(col).where(col, OperatorType.EQUALS_TO, "foob");
        String queryString = new DB2QueryRewriter(null).rewriteQuery(q);
        assertEquals("SELECT sch.foo.bar FROM sch.foo WHERE sch.foo.bar = 'foob'", queryString);
    }

    public void testEscapeFilterItemQuotes() throws Exception {
        Query q = new Query().from(table).select(col).where(col, OperatorType.EQUALS_TO, "foo'bar");
        String queryString = new DB2QueryRewriter(null).rewriteQuery(q);
        assertEquals("SELECT sch.foo.bar FROM sch.foo WHERE sch.foo.bar = 'foo\\'bar'", queryString);

        q = new Query().from(table).select(col)
                .where(col, OperatorType.IN, Arrays.asList("foo'bar", "foo", "bar", "eobject's"));
        queryString = new DB2QueryRewriter(null).rewriteQuery(q);
        assertEquals("SELECT sch.foo.bar FROM sch.foo WHERE sch.foo.bar IN ('foo\\'bar' , 'foo' , 'bar' , 'eobject\\'s')",
                queryString);
    }

    public void testFullyQualifiedColumnNames() throws Exception {
        final MutableSchema schema = new MutableSchema("sch");
        final MutableTable table = new MutableTable("tab", TableType.TABLE, schema);
        final MutableColumn nameColumn = new MutableColumn("name", ColumnType.VARCHAR).setTable(table);
        final MutableColumn ageColumn = new MutableColumn("age", ColumnType.INTEGER).setTable(table);
        schema.addTable(table);
        table.addColumn(nameColumn);
        table.addColumn(ageColumn);

        final Query q = new Query();
        q.select(ageColumn).selectCount();
        q.from(table);
        q.where(ageColumn, OperatorType.GREATER_THAN, 18);
        q.groupBy(ageColumn);
        q.having(FunctionType.COUNT, nameColumn, OperatorType.LESS_THAN, 100);
        q.orderBy(ageColumn);

        final String sql = new DB2QueryRewriter(null).rewriteQuery(q);

        assertEquals("SELECT sch.tab.age, COUNT(*) FROM sch.tab WHERE sch.tab.age > 18 "
                + "GROUP BY sch.tab.age HAVING COUNT(sch.tab.name) < 100 ORDER BY sch.tab.age ASC", sql);
    }

    public void testFullyQualifiedColumnNamesWithFilterItemContainingTimestamp() throws Exception {
        final MutableSchema schema = new MutableSchema("sch");
        final MutableTable table = new MutableTable("tab", TableType.TABLE, schema);
        final MutableColumn nameColumn = new MutableColumn("name", ColumnType.VARCHAR).setTable(table);
        final MutableColumn dateColumn = new MutableColumn("age", ColumnType.TIMESTAMP).setTable(table);
        schema.addTable(table);
        table.addColumn(nameColumn);
        table.addColumn(dateColumn);

        final Query q = new Query();
        q.select(dateColumn).selectCount();
        q.from(table);
        q.where(dateColumn, OperatorType.GREATER_THAN, "2012-10-31 08:09:54");
        q.groupBy(dateColumn);
        q.having(FunctionType.COUNT, nameColumn, OperatorType.LESS_THAN, 100);
        q.orderBy(dateColumn);

        final String sql = new DB2QueryRewriter(null).rewriteQuery(q);

        assertEquals("SELECT sch.tab.age, COUNT(*) FROM sch.tab WHERE sch.tab.age > TIMESTAMP ('2012-10-31 08:09:54') "
                + "GROUP BY sch.tab.age HAVING COUNT(sch.tab.name) < 100 ORDER BY sch.tab.age ASC", sql);
    }
}
