package org.apache.storm.arangodb.bolt;

import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.storm.arangodb.common.mapper.ArangoMapper;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.TupleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.BaseDocument;


/**
 * Basic bolt for updating from ArangoDB.
 * Note: Each ArangoUpdateBolt defined in a topology is tied to a specific collection.
 */
public class ArangoUpdateBolt extends AbstractArangoBolt {
	private static final Logger logger = LoggerFactory.getLogger(ArangoUpdateBolt.class);
    private ArangoMapper mapper;
    
    private String collection;

    /**
     * MongoUpdateBolt Constructor.
     * @param props Arangodb configuration
     * @param database The database where reading/writing data
     * @param collection The collection where reading/writing data
     * @param mapper ArangoMapper converting tuple to an Arango document
     */
    public ArangoUpdateBolt(Properties props, String database, String collection, ArangoMapper mapper) {
        super(props, database);

        Validate.notNull(mapper, "ArangoMapper can not be null");
        this.mapper = mapper;
        this.collection = collection;
    }

    public void execute(Tuple tuple) {
        if (TupleUtils.isTick(tuple)) {
            return;
        }

        try {
        		BaseDocument doc = mapper.toDocument(tuple);
        		logger.info("parsed doc.[doc]"+doc);
        		arangoClient.update(collection, doc.getKey(), doc);
            this.collector.ack(tuple);
        } catch (Exception e) {
            this.collector.reportError(e);
            this.collector.fail(tuple);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

}
