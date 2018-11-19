package com.files.to.couchbase.strategy;

import java.util.List;

public interface IStrategy<T> {

     List<T> parse(String filePath,T entity);
}
