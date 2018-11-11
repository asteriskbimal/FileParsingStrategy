package com.files.to.couchbase.service;

import com.couchbase.client.java.document.JsonDocument;
import com.files.to.couchbase.model.IStrategy;
import com.files.to.couchbase.repository.CouchBaseRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;


@Service
public class CouchBaseService {

    private final CouchBaseRepository filerepository;

    @Autowired
    private final BeanFactory beans;

    private final FileParser fileParser;

    private String filePath;

    private String tableName;

    private String[] key;

    Map<String,IStrategy> strategies;

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
    public CouchBaseService(CouchBaseRepository filerepository, FileParser fileParser,BeanFactory beans) {
        this.filerepository = filerepository;
        this.fileParser=fileParser;
        this.beans=beans;
    }

    public void setStrategy(String strategy){
        fileParser.setStrategy((IStrategy)beans.getBean(strategy));
    }

    public void save(){
        List<JsonDocument> jsonObjects= fileParser.parseFile(getFilePath(),getTableName(),getDataName(),getKey());
        filerepository.save(jsonObjects);
    }

}
