/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.storm.arangodb.common.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.ITuple;
import org.apache.storm.tuple.Values;

import com.arangodb.entity.BaseDocument;

public class SimpleArangoLookupMapper implements ArangoLookupMapper {

    private String[] fields;

    public SimpleArangoLookupMapper(String... fields) {
        this.fields = fields;
    }

    public List<Values> toTuple(ITuple input, BaseDocument doc) {
        Values values = new Values();

        for (String field : fields) {
            if (input.contains(field)) {
                values.add(input.getValueByField(field));
            } else {
                values.add(doc.getProperties().get(field));
            }
        }
        List<Values> result = new ArrayList<Values>();
        result.add(values);
        return result;
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(fields));
    }

    public SimpleArangoLookupMapper withFields(String... fields) {
        this.fields = fields;
        return this;
    }

}
