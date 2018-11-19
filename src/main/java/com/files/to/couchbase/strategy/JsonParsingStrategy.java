package com.files.to.couchbase.strategy;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import flexjson.JSONDeserializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JsonParsingStrategy<T> implements IStrategy<T>{

    private final JSONDeserializer<T> deserializer = new JSONDeserializer<>();

    @Override
    public List<T> parse(String filePath,T entity) {
        List<T> documents = new ArrayList<>();

        try {

            File file = new File(filePath);
            String str = "";
            if (file.exists()) {
                str = new String(Files.readAllBytes(file.toPath()));
            } else {
                System.out.print("file doesn't exist:" + file.getPath());
            }
            JsonObject jsonObj = JsonObject.fromJson(str);
            JsonArray data = (JsonArray) jsonObj.get("data");
            for (Object content : data) {
                T t = deserializer.deserialize(String.valueOf(content), entity.getClass());
                documents.add(t);
            }

        }catch(IOException e){
            System.out.print(e.getMessage());
        }

        return documents;

    }
}
