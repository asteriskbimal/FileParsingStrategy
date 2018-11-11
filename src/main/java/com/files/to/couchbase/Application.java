package com.files.to.couchbase;

import com.files.to.couchbase.model.CsvParsingStrategy;
import com.files.to.couchbase.model.JsonParsingStrategy;
import com.files.to.couchbase.service.CouchBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private CouchBaseService filePersistentService;

    public static void main(String[] args){

        SpringApplication application = new SpringApplication(Application.class);
        // provide file path here
        application.run("D:\\FileParsingStrategy\\src\\main\\resources\\test.txt");
    }

    @Override
    public void run(String... args) {
        // pass the strategy autowired above to service
        // for example for csv file pass csvParsingStrategy and for json file pass jsonParsingStrategy
        // in future we can write other strategy class to pass here for parsing different files
        filePersistentService.setStrategy("jsonParsingStrategy");
        filePersistentService.setFilePath(args[0]);
        filePersistentService.setDataName("data");
        filePersistentService.setTableName("table");
        filePersistentService.setKey("id","name");
        filePersistentService.save();
    }

}
