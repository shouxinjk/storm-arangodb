package org.apache.storm.arangodb.common.mapper;

import java.util.List;
import java.util.Map;

import org.apache.storm.arangodb.common.ArangoUtils;
import org.apache.storm.tuple.ITuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.BaseDocument;

public class SimpleArangoMapper implements ArangoMapper {
	private static final Logger logger = LoggerFactory.getLogger(SimpleArangoMapper.class);
    private String[] fields;

    public SimpleArangoMapper(String... fields) {
        this.fields = fields;
    }

    public BaseDocument toDocument(ITuple tuple) {
        BaseDocument document = new BaseDocument();
        for (String field : fields) {
            if("_key".equalsIgnoreCase(field))
            		document.setKey(""+tuple.getValueByField(field));
            else if("_doc".equalsIgnoreCase(field)){//如果是整个文档，则需要解析得到具体键值对
            		logger.debug("try to put properties.[_doc type]"+tuple.getValueByField("_doc").getClass().getTypeName());
            		if(tuple.getValueByField("_doc") instanceof Map<?,?>) {
            			document.getProperties().putAll((Map<String,Object>)tuple.getValueByField("_doc"));
            		}else {//如果前端传入不是BaseDocument则作为新的键值写入
            			document.addAttribute(field, tuple.getValueByField(field));
            		}
            }else {//否则直接写入键值对
            		document.addAttribute(field, tuple.getValueByField(field));
            }
        }
        return document;
    }

    public BaseDocument toDocumentByKeys(List<Object> keys) {
    		BaseDocument document = new BaseDocument();
        document.addAttribute("_id", ArangoUtils.getId(keys));
        return document;
    }

    public SimpleArangoMapper withFields(String... fields) {
        this.fields = fields;
        return this;
    }
}
