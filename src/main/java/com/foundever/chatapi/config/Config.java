package com.foundever.chatapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Provides common configuration for the whole application. */
@Configuration
public class Config {

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }
}
