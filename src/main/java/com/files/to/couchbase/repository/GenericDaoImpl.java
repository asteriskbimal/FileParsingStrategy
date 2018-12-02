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
        List<String> jsonDocString= new ArrayList<>();
        String compositeKey = createKey(entity);
        RawJsonDocument jsonDoc = serializeToRawJsonDocument(entity, compositeKey);
        return  bucket
                    .async()
                    .upsert(jsonDoc)
                    .map(result -> deserializer.deserialize(result.content(),entity.getClass()))
                    .timeout(3, TimeUnit.SECONDS)
                    .toBlocking()
                    .single();

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
        return bucket
                .async()
                .get(bucketKey, RawJsonDocument.class)
                .map(result -> deserializer.deserialize(result.content(),entity.getClass()))
                .timeout(3, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }
    
    public List<T> getAll(T entity){
        List<T> allEntities=new ArrayList<>();
        String queryString =  "select * from `"+bucket.name()+"` where type=$1";
        ParameterizedN1qlQuery query = N1qlQuery.parameterized(queryString, JsonArray.create().add(entity.getClass().getSimpleName()));
        return bucket
                .async()
                .query(query)
                .flatMap(AsyncN1qlQueryResult::rows)
                .onBackpressureBuffer()
                .map(result -> deserializer.deserialize(result.value().toString(),entity.getClass()))
                .toList()
                .timeout(3, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }

    public List<T> getRecordsWithQueryParams(Class<T> entity, Map<String,String> queryParams){
        String queryString =  "select * from `"+bucket.name()+"` where 1=1";
        StringBuilder sb=new StringBuilder(queryString);
        queryParams.forEach((s1, s2) -> sb.append("and "+s1+"="+s2+" "));
        return bucket
                .async()
                .query(N1qlQuery.simple(sb.toString()))
                .flatMap(AsyncN1qlQueryResult::rows)
                .onBackpressureBuffer()
                .map(result -> deserializer.deserialize(result.value().toString(),entity))
                .toList()
                .timeout(3, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }


}
