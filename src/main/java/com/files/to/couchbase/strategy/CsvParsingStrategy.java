package com.files.to.couchbase.strategy;

import java.util.ArrayList;
import java.util.List;

public class CsvParsingStrategy<T> implements IStrategy<T>{

    @Override
    public List<T> parse(String filePath,T entity) {
        return new ArrayList<>();
    }
}
