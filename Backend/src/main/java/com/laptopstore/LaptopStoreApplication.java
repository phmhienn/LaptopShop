package com.laptopstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LaptopStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaptopStoreApplication.class, args);
    }
}
