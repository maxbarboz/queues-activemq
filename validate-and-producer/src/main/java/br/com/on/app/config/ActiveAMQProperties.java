package br.com.on.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.activemq")
@Getter
@Setter
public class ActiveAMQProperties {

    private String brokerUrl;
    private String user;
    private String password;
    private String nameQueue;

}
