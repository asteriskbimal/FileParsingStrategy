package com.files.to.couchbase.repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import rx.Observable;

import java.util.List;
import java.util.logging.Logger;


@Repository
public class CouchBaseRepository {

    private static final Logger LOGGER = Logger.getLogger(CouchBaseRepository.class.getName());
    private final Bucket bucket;

    @Autowired
    public CouchBaseRepository(Bucket bucket) {
        this.bucket = bucket;
    }

    public void save(List<JsonDocument> documents){
        Observable.from(documents)
                .subscribe((doc)->{
                                    Observable<JsonDocument> s=bucket.async().upsert(doc);
                                    s.map((a)->a.id()).forEach((a)->LOGGER.info(a));
                    },
                        (e)-> {
                                LOGGER.info(e.getMessage());
                                new Exception(e);
                                },
                        ()->LOGGER.info("Success"));
    }

}
