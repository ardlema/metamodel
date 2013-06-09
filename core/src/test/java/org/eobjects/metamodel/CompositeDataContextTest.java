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
package org.eobjects.metamodel;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.query.Query;
import org.eobjects.metamodel.schema.CompositeSchema;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;

public class CompositeDataContextTest extends TestCase {

	/**
	 * A "typical scenario": Use a database and a CSV file to create a query
	 * that joins tables from each
	 */
	public void testBaseCaseCompositeQuery() throws Exception {
		DataContext dc1 = new MockDataContext("schema1", "table1", "");
		DataContext dc2 = new MockDataContext("schema2", "table2", "");

		DataContext composite = new CompositeDataContext(dc1, dc2);

		assertEquals("[schema1, schema2]",
				Arrays.toString(composite.getSchemaNames()));
		assertSame(dc1.getDefaultSchema(), composite.getDefaultSchema());

		DataSet ds = composite.query()
				.from(dc1.getDefaultSchema().getTables()[0]).select("foo")
				.execute();
		List<Object[]> objectArrays = ds.toObjectArrays();
		assertEquals("1", objectArrays.get(0)[0]);
		assertEquals("2", objectArrays.get(1)[0]);
		assertEquals(4, objectArrays.size());
	}

	public void testSchemaNameClashes() throws Exception {
		DataContext dc1 = new MockDataContext("schema", "table1", "");
		DataContext dc2 = new MockDataContext("schema", "table2", "");

		DataContext composite = new CompositeDataContext(dc1, dc2);

		assertEquals("[schema]",
				Arrays.toString(composite.getSchemaNames()));

		Schema schema = composite.getDefaultSchema();
		assertEquals(4, schema.getTableCount());
		assertEquals("[table1, an_empty_table, table2, an_empty_table]",
				Arrays.toString(schema.getTableNames()));
		assertTrue(schema instanceof CompositeSchema);
	}

	public void testJoinSameTableNames() throws Exception {
		DataContext dc1 = new MockDataContext("schema", "table", "dc1");
		DataContext dc2 = new MockDataContext("schema", "table", "dc2");

		DataContext composite = new CompositeDataContext(dc1, dc2);

		assertEquals("[schema]",
				Arrays.toString(composite.getSchemaNames()));

		Schema schema = composite.getDefaultSchema();
		assertEquals(4, schema.getTableCount());
		assertEquals("[table, an_empty_table, table, an_empty_table]", Arrays.toString(schema.getTableNames()));
		assertTrue(schema instanceof CompositeSchema);
		Table[] tables = schema.getTables();
        Table table1 = tables[0];
        Table table2 = tables[2];
        assertNotSame(table1, table2);

		Query q = composite
				.query()
				.from(table1)
				.leftJoin(table2)
				.on(table1.getColumnByName("foo"),
						table2.getColumnByName("foo"))
				.select(table1.getColumnByName("foo"),
						table2.getColumnByName("foo"),
						table1.getColumnByName("bar"),
						table2.getColumnByName("baz")).toQuery();
		assertEquals(
				"SELECT table.foo, table.foo, table.bar, table.baz "
						+ "FROM schema.table LEFT JOIN schema.table ON table.foo = table.foo",
				q.toSql());

		DataSet ds = composite.executeQuery(q);
		assertTrue(ds.next());
		assertEquals("Row[values=[1, 1, hello, world]]", ds.getRow().toString());
		assertTrue(ds.next());
		assertEquals("Row[values=[2, 2, dc1, world]]", ds.getRow().toString());
		assertTrue(ds.next());
		assertEquals("Row[values=[3, 3, hi, dc2]]", ds.getRow().toString());
		assertTrue(ds.next());
		assertEquals("Row[values=[4, 4, yo, world]]", ds.getRow().toString());
		assertFalse(ds.next());
	}
}