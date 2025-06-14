package br.com.on.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "br.com.on.app.entity")
public class AppApplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(AppApplicationConsumer.class, args);
    }
}
