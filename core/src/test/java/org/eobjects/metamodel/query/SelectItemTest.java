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

package org.eobjects.metamodel.query;

import org.eobjects.metamodel.MetaModelTestCase;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.MutableColumn;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;

public class SelectItemTest extends MetaModelTestCase {

    private Schema _schema = getExampleSchema();

    public void testSelectColumnInFromItem() throws Exception {
        final Table projectTable = _schema.getTableByName(TABLE_PROJECT);
        final Column column1 = projectTable.getColumns()[0];
        final Column column2 = projectTable.getColumns()[1];

        Query q = new Query().from(projectTable, "a").from(projectTable, "b");
        q.select(column1, q.getFromClause().getItem(1));
        q.select(column2, q.getFromClause().getItem(0));

        assertEquals("SELECT b.project_id, a.name FROM MetaModelSchema.project a, MetaModelSchema.project b", q.toSql());
    }
    
    public void testToSql() throws Exception {
        SelectItem selectItem = new SelectItem(_schema.getTableByName(TABLE_PROJECT).getColumns()[0]);
        assertEquals("project.project_id", selectItem.toSql());
    }

    public void testSubQuerySelectItem() throws Exception {
        Table projectTable = _schema.getTableByName(TABLE_PROJECT);
        Table roleTable = _schema.getTableByName(TABLE_ROLE);

        Column projectIdColumn = projectTable.getColumnByName(COLUMN_PROJECT_PROJECT_ID);

        FromItem leftSide = new FromItem(projectTable);
        leftSide.setAlias("a");
        SelectItem[] leftOn = new SelectItem[] { new SelectItem(projectIdColumn) };

        Query subQuery = new Query();
        FromItem subQueryFrom = new FromItem(roleTable);
        subQueryFrom.setAlias("c");
        subQuery.from(subQueryFrom);
        Column[] columns = roleTable.getColumns();
        subQuery.select(columns);

        SelectItem subQuerySelectItem = subQuery.getSelectClause().getSelectItem(columns[1]);
        FromItem rightSide = new FromItem(subQuery);
        rightSide.setAlias("b");
        SelectItem[] rightOn = new SelectItem[] { subQuerySelectItem };
        FromItem from = new FromItem(JoinType.LEFT, leftSide, rightSide, leftOn, rightOn);

        assertEquals(
                "MetaModelSchema.project a LEFT JOIN (SELECT c.contributor_id, c.project_id, c.name FROM MetaModelSchema.role c) b ON a.project_id = b.project_id",
                from.toString());

        Query q = new Query();
        q.from(from);
        try {
            new SelectItem(subQuerySelectItem, from);
            fail("Exception should have been thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals("Only sub-query based FromItems allowed.", e.getMessage());
        }

        q.select(new SelectItem(subQuerySelectItem, rightSide));
        assertEquals(
                "SELECT b.project_id FROM MetaModelSchema.project a LEFT JOIN (SELECT c.contributor_id, c.project_id, c.name FROM MetaModelSchema.role c) b ON a.project_id = b.project_id",
                q.toString());
    }

    public void testGetSuperQueryAlias() throws Exception {
        SelectItem item = new SelectItem(FunctionType.COUNT, "*", "").setAlias(null);
        assertEquals("COUNT(*)", item.getSameQueryAlias());
        assertEquals("COUNT(*)", item.getSuperQueryAlias());
        
        item = new SelectItem(FunctionType.SUM, new MutableColumn("foo"));
        assertEquals("SUM(foo)", item.getSameQueryAlias());
        assertEquals("SUM(foo)", item.getSuperQueryAlias());
    }
}