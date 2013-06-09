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
package org.eobjects.metamodel.drop;

import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;

public interface TableDroppable {

    /**
     * Determines whether table drop is supported
     * 
     * @return true if table drop is supported
     */
    public boolean isDropTableSupported();

    public TableDropBuilder dropTable(Schema schema, String tableName) throws IllegalArgumentException,
            IllegalStateException, UnsupportedOperationException;

    public TableDropBuilder dropTable(String schemaName, String tableName) throws IllegalArgumentException,
            IllegalStateException, UnsupportedOperationException;

    public TableDropBuilder dropTable(String tableName) throws IllegalArgumentException, IllegalStateException,
            UnsupportedOperationException;

    public TableDropBuilder dropTable(Table table) throws IllegalArgumentException, IllegalStateException,
            UnsupportedOperationException;
}
