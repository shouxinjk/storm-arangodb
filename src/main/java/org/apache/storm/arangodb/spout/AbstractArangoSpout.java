package org.apache.storm.arangodb.spout;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.apache.storm.Config;
import org.apache.storm.arangodb.common.ArangoDbClient;
import org.apache.storm.arangodb.common.mapper.ArangoLookupMapper;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class AbstractArangoSpout extends BaseRichSpout implements IRichSpout {
    boolean isDistributed;
    SpoutOutputCollector collector;
    protected Properties props;
    protected String database;
    protected String query;
    protected String[] fields;
    protected Map<String,Object> bindVars;
	protected transient ArangoDbClient arangoClient;
    private static final Logger logger = Logger.getLogger(AbstractArangoSpout.class);
    
    public AbstractArangoSpout(Properties props,String database) {
		Validate.notEmpty(props.getProperty("arangodb.host"), "host can not be blank or null");
	    Validate.notEmpty(props.getProperty("arangodb.port"), "port can not be blank or null");
	    Validate.notEmpty(props.getProperty("arangodb.username"), "username can not be blank or null");
	    Validate.notEmpty(props.getProperty("arangodb.password"), "password can not be blank or null");
	    this.props = props;
	    this.database = database;
	}
	
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    public void close(){
    		arangoClient.close();
    }

    public void ack(Object msgId) {
    	//do nothing
    }

    public void fail(Object msgId) {
    	//do nothing
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

	public void nextTuple() {
		// TODO Auto-generated method stub
		
	}

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}
}