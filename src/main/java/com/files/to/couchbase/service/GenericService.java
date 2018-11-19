package com.files.to.couchbase.service;

import com.files.to.couchbase.repository.GenericDaoImpl;
import com.files.to.couchbase.util.FileParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GenericService<T> {

    private final GenericDaoImpl<T> genericDaoImpl;

    private final FileParser<T> fileParser;

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Autowired
    public GenericService(GenericDaoImpl<T> genericDaoImpl, FileParser<T> fileParser) {
        this.genericDaoImpl = genericDaoImpl;
        this.fileParser=fileParser;
    }

    public void save(T entity){
        List<T> entities= fileParser.parseFile(getFilePath(),entity);
        genericDaoImpl.upsertAll(entities);
    }


}
