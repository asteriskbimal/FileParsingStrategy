package com.files.to.couchbase;

import com.files.to.couchbase.model.FCU001Q;
import com.files.to.couchbase.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private GenericService<FCU001Q> fcuoo1qService;

    public static void main(String[] args){

        SpringApplication application = new SpringApplication(Application.class);
        // provide file path here
        application.run("D:\\FileParsingStrategy\\src\\main\\resources\\test.json");

    }

    @Override
    public void run(String... args) {
        // pass the strategy autowired above to service
        // for example for csv file pass csvParsingStrategy and for json file pass jsonParsingStrategy
        // in future we can write other strategy class to pass here for parsing different files
        FCU001Q entity=new FCU001Q();
        fcuoo1qService.setFilePath(args[0]);
        fcuoo1qService.save(entity);
    }

}
