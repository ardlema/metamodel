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
package org.apache.metamodel.elasticsearch;

import java.util.Date;
import java.util.Map;

import org.apache.metamodel.data.DataSetHeader;
import org.apache.metamodel.data.DefaultRow;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.util.TimeComparator;

/**
 * Shared/common util functions for the ElasticSearch MetaModel module.
 */
final class ElasticSearchUtils {

    public static Row createRow(Map<String, Object> sourceMap, String documentId, DataSetHeader header) {
        final Object[] values = new Object[header.size()];
        for (int i = 0; i < values.length; i++) {
            final SelectItem selectItem = header.getSelectItem(i);
            final Column column = selectItem.getColumn();

            assert column != null;
            assert selectItem.getFunction() == null;

            if (column.isPrimaryKey()) {
                values[i] = documentId;
            } else {
                Object value = sourceMap.get(column.getName());

                if (column.getType() == ColumnType.DATE) {
                    Date valueToDate = TimeComparator.toDate(value);
                    if (valueToDate == null) {
                        values[i] = value;
                    } else {
                        values[i] = valueToDate;
                    }
                } else {
                    values[i] = value;
                }
            }
        }

        return new DefaultRow(header, values);
    }
}
