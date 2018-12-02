package com.files.to.couchbase.repository;

import java.util.List;

public interface GenericDao<T> {

    Mono<T> upsert(T entity);

    Flux<T> upsertAll(List<T> entities);

    Mono<T> get(String bucketKey,Class<T> entity);

    Flux<T> getAll(T entity);

    Flux<T> getRecordsWithQueryParams(Class<T> entity, Map<String,String> queryParams);

}
