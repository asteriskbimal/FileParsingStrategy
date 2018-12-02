package com.files.to.couchbase.repository;

import java.util.List;

public interface GenericDao<T> {

    T upsert(T entity);

     List<T> upsertAll(List<T> entities);

    T get(String bucketKey,Class<T> entity);

    List<T> getAll(T entity);
    
     List<T> getRecordsWithQueryParams(Class<T> entity, Map<String,String> queryParams);

}
