package com.files.to.couchbase.model;

import com.couchbase.client.java.document.json.JsonObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParsingStrategy implements IStrategy{

    @Override
    public List<JsonObject> parse(String filePath,String table_name, String data, String... key) {
        return new ArrayList<>();
    }
}
