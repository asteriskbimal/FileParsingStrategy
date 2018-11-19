package com.files.to.couchbase.util;

import com.files.to.couchbase.strategy.CsvParsingStrategy;
import com.files.to.couchbase.strategy.IStrategy;
import com.files.to.couchbase.strategy.JsonParsingStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class FileParser<T> {

    private final Map<String,IStrategy> strategies=new HashMap<>();

    @PostConstruct
    public void init(){
        strategies.put("json",new JsonParsingStrategy<T>());
        strategies.put("csv",new CsvParsingStrategy<T>());
    }

    public List<T> parseFile(String filePath, T entity){
        String[] strategyType=filePath.split("[.]");
        IStrategy<T> strategy=strategies.get(strategyType[1]);
        return strategy.parse(filePath,entity);
    }

}
