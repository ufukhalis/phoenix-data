package io.github.ufukhalis.phoenix.config;

import io.github.ufukhalis.phoenix.data.PhoenixDataConnection;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PhoenixDataProperties.class)
public class PhoenixDataConfig {
    private PhoenixDataProperties phoenixDataProperties;

    public PhoenixDataConfig(PhoenixDataProperties phoenixDataProperties) {
        this.phoenixDataProperties = phoenixDataProperties;
    }

    @Bean
    public PhoenixDataConnection getPhoenixDataConnection() {
        return new PhoenixDataConnection(phoenixDataProperties.getJdbcUrl(), phoenixDataProperties.getMaxConnection(), phoenixDataProperties.getMinConnection());
    }

}
