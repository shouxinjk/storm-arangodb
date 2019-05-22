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

import org.apache.storm.tuple.ITuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.BaseDocument;


public class SimpleArangoUpdateMapper extends SimpleArangoMapper implements ArangoUpdateMapper {
	private static final Logger logger = LoggerFactory.getLogger(SimpleArangoUpdateMapper.class);
    private String[] fields;

    public SimpleArangoUpdateMapper(String... fields) {
        this.fields = fields;
    }

    @Override
    public BaseDocument toDocument(ITuple tuple) {
    	logger.error("now update.[data]"+tuple);
        BaseDocument document = new BaseDocument();
        for (String field : fields) {
        		if("_key".equalsIgnoreCase(field))
        			document.setKey(tuple.getStringByField(field));
        		else {
        			if(tuple.contains(field))
        				document.getProperties().put(field, tuple.getValueByField(field));
        		}
        }
        return document;
    }

    public SimpleArangoUpdateMapper withFields(String... fields) {
        this.fields = fields;
        return this;
    }
}
