package com.files.to.couchbase.repository;

import java.util.List;

public interface GenericDao<T> {

    T upsert(T entity) throws IllegalAccessException,InstantiationException;
     List<T> upsertAll(List<T> entities) throws IllegalAccessException,InstantiationException;
}
