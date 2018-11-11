package com.files.to.couchbase.model;

import com.couchbase.client.java.document.json.JsonObject;

import java.util.List;

public interface IStrategy<T> {

     List<JsonObject> parse(String filePath,String table_name, String data, String... key);
}
