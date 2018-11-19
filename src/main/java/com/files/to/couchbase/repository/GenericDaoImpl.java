package com.files.to.couchbase.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.*;
import com.files.to.couchbase.model.BucketKey;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private String createKey(T entity){

        String compositeKey = entity.getClass().getSimpleName();
        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(BucketKey.class)) {
                    f.setAccessible(true);
                    String value = (String) f.get(entity);
                    compositeKey = compositeKey + ":" + value;
                }
            }
        }catch(IllegalAccessException e){
            logger.error(e.getMessage());
        }
        return compositeKey;
    }

    @Override
    public T upsert(T entity){
        String compositeKey = createKey(entity);
        RawJsonDocument jsonDoc = serializeToRawJsonDocument(entity, compositeKey);
        Observable<RawJsonDocument> retJsonDoc = bucket.async().upsert(jsonDoc);
        T t = deserializer.deserialize(retJsonDoc.toString(), entity.getClass());
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
                .subscribe(entity->ent.add(upsert(entity)),
                        (e)->logger.error(e.getMessage()),
                        ()->logger.info("Success"));
        return ent;
    }

    public T get(String bucketKey,Class<T> entity) {
        Observable<RawJsonDocument> retJsonDoc = bucket.async().get(bucketKey, RawJsonDocument.class);
        T t = deserializer.deserialize(retJsonDoc.toString(), entity);
        return t;
    }
    public List<T> getAll(T entity){
        List<T> allEntities=new ArrayList<>();
        String queryString =  "select * from `"+bucket.name()+"` where type=$1";
        ParameterizedN1qlQuery query = N1qlQuery.parameterized(queryString, JsonArray.create().add(entity.getClass().getSimpleName()));
        N1qlQueryResult result = bucket.query(query);
        result.allRows().stream().forEach((row)->{
            T t = deserializer.deserialize(row.value().get(bucket.name()).toString(), entity.getClass());
            allEntities.add(t);
        });
        return allEntities;
    }

}