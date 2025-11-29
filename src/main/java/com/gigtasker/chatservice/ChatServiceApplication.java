package com.gigtasker.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatServiceApplication {

    private ChatServiceApplication() {}

    static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }

}
