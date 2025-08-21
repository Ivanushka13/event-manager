package ru.davydov.eventmanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EventMangerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventMangerApplication.class, args);
    }

}
