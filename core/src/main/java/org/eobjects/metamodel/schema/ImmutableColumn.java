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
package org.eobjects.metamodel.schema;

import java.io.Serializable;

/**
 * Immutable implementation of the Column interface.
 * 
 * It is not intended to be instantiated on it's own. Rather, use the
 * constructor in ImmutableSchema.
 * 
 * @see ImmutableSchema
 * 
 * @author Kasper Sørensen
 */
public final class ImmutableColumn extends AbstractColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int columnNumber;
    private final ColumnType type;
    private final Table table;
    private final Boolean nullable;
    private final String remarks;
    private final Integer columnSize;
    private final String nativeType;
    private final boolean indexed;
    private final boolean primaryKey;
    private final String name;
    private final String quote;

    /**
     * Constructs a new {@link ImmutableColumn}.
     * 
     * @param name
     *            the name of the column
     * @param type
     *            the type of the column
     * @param table
     *            the table which the constructed column will pertain to
     * @param columnNumber
     *            the column number of the column
     * @param columnSize
     *            the size of the column
     * @param nativeType
     *            the native type of the column
     * @param nullable
     *            whether the column's values are nullable
     * @param remarks
     *            the remarks of the column
     * @param indexed
     *            whether the column is indexed or not
     * @param quote
     *            the quote character(s) of the column
     * @param primaryKey
     *            whether the column is a primary key or not
     */
    public ImmutableColumn(String name, ColumnType type, Table table, int columnNumber, Integer columnSize,
            String nativeType, Boolean nullable, String remarks, boolean indexed, String quote, boolean primaryKey) {
        this.name = name;
        this.type = type;
        this.table = table;
        this.columnNumber = columnNumber;
        this.columnSize = columnSize;
        this.nativeType = nativeType;
        this.nullable = nullable;
        this.remarks = remarks;
        this.indexed = indexed;
        this.quote = quote;
        this.primaryKey = primaryKey;
    }

    /**
     * Constructs an {@link ImmutableColumn} based on an existing column and a
     * table.
     * 
     * @param column
     *            the column to immitate
     * @param table
     *            the table that the constructed column will pertain to
     */
    public ImmutableColumn(Column column, Table table) {
        this.name = column.getName();
        this.type = column.getType();
        this.table = table;
        this.columnNumber = column.getColumnNumber();
        this.columnSize = column.getColumnSize();
        this.nativeType = column.getNativeType();
        this.nullable = column.isNullable();
        this.remarks = column.getRemarks();
        this.indexed = column.isIndexed();
        this.quote = column.getQuote();
        this.primaryKey = column.isPrimaryKey();
    }

    protected ImmutableColumn(Column column, ImmutableTable table) {
        this(column.getName(), column.getType(), table, column.getColumnNumber(), column.getColumnSize(), column
                .getNativeType(), column.isNullable(), column.getRemarks(), column.isIndexed(), column.getQuote(),
                column.isPrimaryKey());
    }

    @Override
    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public ColumnType getType() {
        return type;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public Boolean isNullable() {
        return nullable;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public Integer getColumnSize() {
        return columnSize;
    }

    @Override
    public String getNativeType() {
        return nativeType;
    }

    @Override
    public boolean isIndexed() {
        return indexed;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public String getQuote() {
        return quote;
    }
}
