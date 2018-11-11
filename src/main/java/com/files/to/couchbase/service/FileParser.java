package com.files.to.couchbase.service;

import com.couchbase.client.java.document.JsonDocument;
import com.files.to.couchbase.model.IStrategy;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FileParser {

    private IStrategy iStrategy;

    public void setStrategy( IStrategy strategy) {
        iStrategy=strategy;
    }

    public List<JsonDocument> parseFile(String filePath,String table_name, String data_name, String... key){
        return iStrategy.parse(filePath,table_name,data_name,key);
    }

}
