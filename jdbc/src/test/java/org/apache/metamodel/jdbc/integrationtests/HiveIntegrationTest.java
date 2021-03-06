/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.metamodel.jdbc.integrationtests;

import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.UpdateScript;
import org.apache.metamodel.create.CreateTable;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.drop.DropTable;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.jdbc.dialects.HiveQueryRewriter;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;

public class HiveIntegrationTest extends AbstractJdbIntegrationTest {

    @Override
    protected String getPropertyPrefix() {
        return "hive";
    }

    public void testGetSchema() throws Exception {
        if (!isConfigured()) {
            return;
        }
        final JdbcDataContext dataContext = getDataContext();

        final Schema schema = dataContext.getSchemaByName("default");
        assertEquals("Schema[name=default]", schema.toString());
    }
    
    public void testUseCorrectRewriter() throws Exception {
        if (!isConfigured()) {
            return;
        }
        final JdbcDataContext dataContext = getDataContext();

        assertTrue(dataContext.getQueryRewriter() instanceof HiveQueryRewriter);
    }

    public void testCreateInsertQueryAndDrop() throws Exception {
        if (!isConfigured()) {
            return;
        }
        final JdbcDataContext dataContext = getDataContext();

        final String tableName = "metamodel_" + System.currentTimeMillis();
        final Schema schema = dataContext.getDefaultSchema();

        dataContext.executeUpdate(new CreateTable(schema, tableName).withColumn("foo").ofType(ColumnType.STRING)
                .withColumn("bar").ofType(ColumnType.INTEGER));
        try {
            final Table table = dataContext.getTableByQualifiedLabel(tableName);
            assertNotNull(table);
            
            dataContext.executeUpdate(new UpdateScript() {
                
                @Override
                public void run(UpdateCallback callback) {
                    callback.insertInto(table).value("foo", "Hello world").value("bar", 42).execute();
                    callback.insertInto(table).value("foo", "Lorem ipsum").value("bar", 42).execute();
                    callback.insertInto(table).value("foo", "Apache").value("bar", 43).execute();
                    callback.insertInto(table).value("foo", "MetaModel").value("bar", 44).execute();
                }
            });
            
            final DataSet ds1 = dataContext.query().from(table).selectCount().execute();
            assertTrue(ds1.next());
            assertEquals(4l, ds1.getRow().getValue(0));
            assertFalse(ds1.next());
            ds1.close();
            
            final DataSet ds2 = dataContext.query().from(table).selectAll().where("bar").eq(42).execute();
            assertTrue(ds2.next());
            assertEquals("Row[values=[Hello world, 42]]", ds2.getRow().toString());
            assertTrue(ds2.next());
            assertEquals("Row[values=[Lorem ipsum, 42]]", ds2.getRow().toString());
            assertFalse(ds2.next());
            ds2.close();
        } finally {
            dataContext.executeUpdate(new DropTable(schema, tableName));
        }
    }
}
