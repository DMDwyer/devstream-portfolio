package com.dmdwyer.devstream.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter flagEvaluationCounter(MeterRegistry meterRegistry) {
        return Counter
                .builder("flag_evaluations_total")
                .description("Number of times flags have been evaluated")
                .register(meterRegistry);
    }
}