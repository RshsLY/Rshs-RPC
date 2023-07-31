package com.rshs.use.server;


import com.rshs.api.annotation.RshsScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@RshsScan(basePackage = {"com.rshs.use.server.provider"})
@SpringBootApplication
public class UseServerApp1 {
    public static void main(String[] args) {
        SpringApplication.run(UseServerApp1.class,args);
        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

}
