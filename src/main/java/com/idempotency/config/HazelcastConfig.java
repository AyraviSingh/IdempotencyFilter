package com.idempotency.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;

@Configuration
@RequiredArgsConstructor
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        try {
            // Load Hazelcast configuration from YAML file
            Resource resource = new ClassPathResource("hazelcast.yaml");
            ResourcePropertySource propertySource = new ResourcePropertySource(resource);

            // Iterate through the loaded properties and set them in the Hazelcast Config
            for (String key : propertySource.getPropertyNames()) {
                config.setProperty(key, (String) propertySource.getProperty(key));
            }
        } catch (Exception e) {
            // Handle the exception (e.g., log it) if the YAML file is not found or there is an error loading it.
        }

        return Hazelcast.newHazelcastInstance(config);
    }
}
