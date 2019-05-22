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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.storm.arangodb.common.mapper.ArangoMapper;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.BatchHelper;
import org.apache.storm.utils.TupleUtils;

import com.arangodb.entity.BaseDocument;

public class ArangoInsertBolt extends AbstractArangoBolt {

    private static final int DEFAULT_FLUSH_INTERVAL_SECS = 1;
    private static final int DEFAULT_BATCH_SIZE = 10;

    private ArangoMapper mapper;

    private boolean ordered = true;  //default is ordered.

    private int batchSize = DEFAULT_BATCH_SIZE;
    
    private String collection;

    private BatchHelper batchHelper;

    private int flushIntervalSecs = DEFAULT_FLUSH_INTERVAL_SECS;

    public ArangoInsertBolt(Properties props, String database, String collection, ArangoMapper mapper) {
        super(props, database);

        Validate.notNull(mapper, "ArangoMapper can not be null");
        this.mapper = mapper;
        this.collection = collection;
    }

    public void execute(Tuple tuple) {
        try {
            if (batchHelper.shouldHandle(tuple)) {
                batchHelper.addBatch(tuple);
            }

            if (batchHelper.shouldFlush()) {
                flushTuples();
                batchHelper.ack();
            }
        } catch (Exception e) {
            batchHelper.fail(e);
        }
    }

    private void flushTuples() {
        List<BaseDocument> docs = new LinkedList<BaseDocument>();
        for (Tuple t : batchHelper.getBatchTuples()) {
            BaseDocument doc = mapper.toDocument(t);
            docs.add(doc);
        }
        arangoClient.insert(collection,docs);
    }

    public ArangoInsertBolt withCollection(String collection) {
        this.collection = collection;
        return this;
    }
    
    public ArangoInsertBolt withBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public ArangoInsertBolt withFlushIntervalSecs(int flushIntervalSecs) {
        this.flushIntervalSecs = flushIntervalSecs;
        return this;
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return TupleUtils.putTickFrequencyIntoComponentConfig(super.getComponentConfiguration(), flushIntervalSecs);
    }

    @Override
    public void prepare(Map topoConf, TopologyContext context,
            OutputCollector collector) {
        super.prepare(topoConf, context, collector);
        this.batchHelper = new BatchHelper(batchSize, collector);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        
    }

}
