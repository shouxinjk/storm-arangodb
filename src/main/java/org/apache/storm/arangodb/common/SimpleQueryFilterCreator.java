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

package org.apache.storm.arangodb.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.tuple.ITuple;


public class SimpleQueryFilterCreator implements QueryFilterCreator {

    private String[] fields;
    private Map<String,Object> bindVars = new HashMap<String,Object>();
    
    public Map<String,Object> createFilter(ITuple tuple) {
    		for(String field:fields) {
    			bindVars.put(field, tuple.getValueByField(field));
    		}
    		return bindVars;
    }

    public Map<String,Object> createFilterByKeys(List<Object> keys) {
//        return Filters.eq("_id", MongoUtils.getId(keys));
    		return bindVars;
    }

    public SimpleQueryFilterCreator withField(String... fields) {
        this.fields = fields;
        return this;
    }

}
