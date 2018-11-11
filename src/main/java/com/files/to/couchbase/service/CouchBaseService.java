package com.files.to.couchbase.service;

import com.couchbase.client.java.document.JsonDocument;
import com.files.to.couchbase.model.IStrategy;
import com.files.to.couchbase.repository.CouchBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CouchBaseService {

    private final CouchBaseRepository filerepository;

    private final FileParser fileParser;

    private String filePath;

    private String tableName;

    private String[] key;

    public String[] getKey() {
        return key;
    }

    public void setKey(String... key) {
        this.key = key;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    private String dataName;

    @Autowired
    public CouchBaseService(CouchBaseRepository filerepository, FileParser fileParser) {
        this.filerepository = filerepository;
        this.fileParser=fileParser;
    }

    public void setStrategy(IStrategy strategy){
        fileParser.setStrategy(strategy);
    }

    public void save(){
        List<JsonDocument> jsonObjects= fileParser.parseFile(getFilePath(),getTableName(),getDataName(),getKey());
        filerepository.save(jsonObjects);
    }

}
