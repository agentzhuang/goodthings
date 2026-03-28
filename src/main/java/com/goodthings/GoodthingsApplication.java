package com.goodthings;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.goodthings.mapper")
public class GoodthingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodthingsApplication.class, args);
    }
}
