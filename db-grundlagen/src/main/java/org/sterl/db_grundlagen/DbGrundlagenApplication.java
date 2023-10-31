package org.sterl.db_grundlagen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DbGrundlagenApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbGrundlagenApplication.class, args);
    }

}
