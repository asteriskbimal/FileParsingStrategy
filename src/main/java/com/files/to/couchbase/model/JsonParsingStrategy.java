package com.files.to.couchbase.model;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.transcoder.JsonTranscoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonParsingStrategy implements IStrategy{

    @Override
    public List<JsonDocument> parse(String filePath,String table_name, String data_name, String... key) {
        List<JsonDocument> documents = new ArrayList<JsonDocument>();

        try {

            File file = new File(filePath);
            String str = "";
            if (file.exists()) {
                str = new String(Files.readAllBytes(file.toPath()));
            } else {
                System.out.print("file doesn't exist:" + file.getPath());
            }
            JsonObject jsonObj = JsonObject.fromJson(str);
            String tableName = (String) jsonObj.get(table_name);
            JsonArray data = (JsonArray) jsonObj.get(data_name);
            for (Object content : data) {
                String keyString = tableName + createKey((JsonObject)content,key);
                ((JsonObject) content).put(table_name,tableName);
                documents.add(JsonDocument.create(keyString, (JsonObject) content));
            }

        }catch(IOException e){
            System.out.print(e.getMessage());
        }

        return documents;

    }

    public String createKey(JsonObject content,String... key){
        StringBuffer sb=new StringBuffer();
        for(String s:key){
            sb.append("-"+content.get(s));
        }
        return sb.toString();
    }
}
