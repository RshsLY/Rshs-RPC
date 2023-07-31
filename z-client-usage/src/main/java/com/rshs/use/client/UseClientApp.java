package com.rshs.use.client;


import com.rshs.api.annotation.RshsScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@RshsScan(basePackage = {"com.rshs.use.client.consumer"})
@SpringBootApplication
public class UseClientApp {
    public static void main(String[] args) {
        SpringApplication.run(UseClientApp.class,args);
    }

}
