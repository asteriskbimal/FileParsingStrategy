package com.files.to.couchbase.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.files.to.couchbase.model.BucketKey;
import flexjson.JSONDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import flexjson.JSONSerializer;
import org.springframework.stereotype.Repository;
import rx.Observable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GenericDaoImpl<T> implements GenericDao<T> {

    private static Logger logger = LoggerFactory.getLogger(GenericDaoImpl.class);

    private final Bucket bucket;

    @Autowired
    public GenericDaoImpl(Bucket bucket) {
        this.bucket = bucket;
    }

    JSONSerializer serializer = new JSONSerializer();
    JSONDeserializer<T> deserializer = new JSONDeserializer<>();

    private String createKey(T entity) throws IllegalAccessException,InstantiationException{
        String compositeKey = entity.getClass().getSimpleName();
        Field[] fields =entity.getClass().getDeclaredFields();
        for(Field f:fields){
            if(f.isAnnotationPresent(BucketKey.class)){
                f.setAccessible(true);
                String value = (String)f.get(entity);
                compositeKey = compositeKey + ":" + value;
            }
        }
        return compositeKey;
    }

    @Override
    public T upsert(T entity) throws IllegalAccessException,InstantiationException{
        String compositeKey = createKey(entity);
        RawJsonDocument jsonDoc = serializeToRawJsonDocument(entity, compositeKey);
        RawJsonDocument retJsonDoc = bucket.upsert(jsonDoc);
        T t = deserializer.deserialize(retJsonDoc.content(), entity.getClass());
        return t;
    }

    public RawJsonDocument serializeToRawJsonDocument(T entity,String compositeKey){
        String jsonString = serializer.exclude("class").deepSerialize(entity);
        RawJsonDocument jsonDoc = RawJsonDocument.create(compositeKey, jsonString);
        return jsonDoc;
    }

    @Override
    public List<T> upsertAll(List<T> entities){
        List<T> ent =new ArrayList<>();
        Observable.from(entities)
                .subscribe(entity->{
                    try{ ent.add(upsert(entity));
                    }catch(IllegalAccessException|InstantiationException e){
                        Observable.error(e);
                    } },(e)->logger.error(e.getMessage()),
                        ()->logger.info("Success"));

        return ent;
    }

}