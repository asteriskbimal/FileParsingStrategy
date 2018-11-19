package com.files.to.couchbase.model;

import lombok.Data;

@Data
public class FCU001Q {
    @BucketKey
    private String id;
    @BucketKey
    private String name;
    private String address;
    private String company;
}
