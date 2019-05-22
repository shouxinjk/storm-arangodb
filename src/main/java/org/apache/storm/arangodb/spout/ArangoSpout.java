package org.apache.storm.arangodb.spout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.arangodb.common.ArangoDbClient;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.BaseDocument;

public class ArangoSpout extends AbstractArangoSpout {
	private static final Logger logger = LoggerFactory.getLogger(ArangoSpout.class);
	private static final long serialVersionUID = 3491834835912620171L;

	public ArangoSpout(Properties props, String database) {
		super(props, database);
	}
	
	public ArangoSpout withQuery(String query) {
		this.query = query;
		this.bindVars = new HashMap<String,Object>();
		return this;
	}
	
	public ArangoSpout withQuery(String query, Map<String,Object> bindVars) {
		this.query = query;
		this.bindVars = bindVars;
		return this;
	}
	
	public ArangoSpout withFields(String... fields) {
		this.fields = fields;
		return this;
	}
	
	@Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
		this.arangoClient = new ArangoDbClient(props,database);
		logger.debug("open...[arangoDbClient]"+arangoClient);
    }
    
	@Override
    public void nextTuple() {
        try {
	        //find document from arangodb
        		logger.debug("query data.[query]"+query+"[bindvars]"+bindVars);
	        List<BaseDocument> docs = arangoClient.query(query, bindVars, BaseDocument.class);
	        logger.debug("query data.[total]"+docs.size());
	        //get storm values and emit
	        for(BaseDocument doc:docs) {
	        		logger.debug("query data.[doc]"+doc);
	            Values values = new Values();
	            if(fields.length>0) {//如果指定属性则仅返回指定数据
		            for (String field : fields) {
		            		if("_key".equalsIgnoreCase(field))//将_key作为第一个
		        	            values.add(doc.getKey());
		            		else if("_doc".equalsIgnoreCase(field)) {//将所有数据doc作为一个字段
		            			values.add(doc.getProperties());
		            		}else
		            			values.add(doc.getProperties().get(field));
		            }
	            }else {//do nothing
	            	logger.debug("no more docs.");
	            }
	            this.collector.emit(values);
	        }
	    } catch (Exception e) {
	        this.collector.reportError(e);
	    }
        Thread.yield();
    }
    
	@Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
		logger.debug("declare output fields...[fields]"+fields);
        declarer.declare(new Fields(fields));
    }

}
