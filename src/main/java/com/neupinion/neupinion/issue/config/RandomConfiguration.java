package com.neupinion.neupinion.issue.config;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RandomConfiguration {

    @Bean
    public Random random() {
        return new Random();
    }
}
