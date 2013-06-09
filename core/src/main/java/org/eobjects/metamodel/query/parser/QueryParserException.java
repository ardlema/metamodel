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
package org.eobjects.metamodel.query.parser;

import org.eobjects.metamodel.MetaModelException;

/**
 * Subtype of {@link MetaModelException} which indicate a problem in parsing a
 * query passed to the {@link QueryParser}.
 */
public class QueryParserException extends MetaModelException {

    private static final long serialVersionUID = 1L;

    public QueryParserException() {
        super();
    }

    public QueryParserException(Exception cause) {
        super(cause);
    }

    public QueryParserException(String message, Exception cause) {
        super(message, cause);
    }

    public QueryParserException(String message) {
        super(message);
    }
}
