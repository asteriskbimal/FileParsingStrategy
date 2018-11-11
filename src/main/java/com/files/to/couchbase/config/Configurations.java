package com.files.to.couchbase.config;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configurations {


    @Value("${couchbase.bucket.name}")
    private String bucket;

    @Value("${couchbase.bucket.username}")
    private String username;

    @Value("${couchbase.bucket.password}")
    private String password;

    @Value("${couchbase.bucket.host}")
    private String host;

    @Autowired
    private ApplicationContext applicationContext;

    public @Bean Bucket loginBucket() {

        Cluster cluster = CouchbaseCluster.create(host);
        cluster.authenticate(username, password);
        Bucket b = cluster.openBucket(bucket);
        return b;

    }

}
