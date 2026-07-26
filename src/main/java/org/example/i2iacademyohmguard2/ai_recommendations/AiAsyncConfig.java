package org.example.i2iacademyohmguard2.ai_recommendations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AI oneri uretiminin, Kafka consumer thread'ini bloklamadan calismasi icin
 * ayrilmis async altyapisi.
 */
@Configuration
@EnableAsync
public class AiAsyncConfig {

    @Bean(name = "aiRecommendationExecutor")
    public Executor aiRecommendationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ai-reco-");
        executor.initialize();
        return executor;
    }
}
