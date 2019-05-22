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

package org.apache.storm.arangodb.bolt;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.storm.arangodb.common.QueryFilterCreator;
import org.apache.storm.arangodb.common.mapper.ArangoLookupMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.TupleUtils;

import com.arangodb.entity.BaseDocument;

public class ArangoLookupBolt extends AbstractArangoBolt {

    private ArangoLookupMapper mapper;
    private String query;
    private QueryFilterCreator filterCreator;

    public ArangoLookupBolt(Properties props, String database, String query, QueryFilterCreator filterCreator, ArangoLookupMapper mapper) {
        super(props, database);

        Validate.notNull(query, "query can not be null");
        Validate.notNull(mapper, "ArangoLookupMapper can not be null");

        this.query = query;
        this.filterCreator = filterCreator;
        this.mapper = mapper;
    }

    public void execute(Tuple tuple) {
        if (TupleUtils.isTick(tuple)) {
            return;
        }

        try {
        		//create bindvars
        		Map<String,Object> bindVars = filterCreator.createFilter(tuple);
            //find document from arangodb
            List<BaseDocument> docs = arangoClient.query(query, bindVars, BaseDocument.class);
            //get storm values and emit
            for(BaseDocument doc:docs) {
	            List<Values> valuesList = mapper.toTuple(tuple, doc);
	            for (Values values : valuesList) {
	                this.collector.emit(tuple, values);
	            }
            }
            this.collector.ack(tuple);
        } catch (Exception e) {
            this.collector.reportError(e);
            this.collector.fail(tuple);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        mapper.declareOutputFields(declarer);
    }

}
