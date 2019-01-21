package io.github.ufukhalis.phoenix.config;

import io.github.ufukhalis.phoenix.mapper.TableStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("phoenix.data")
public class PhoenixDataProperties {

    private String jdbcUrl;
    private int minConnection;
    private int maxConnection;
    private String basePackage = "";
    private String tableStrategy = TableStrategy.CREATE.name();

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setTableStrategy(String tableStrategy) {
        this.tableStrategy = tableStrategy;
    }

    public String getTableStrategy() {
        return tableStrategy;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMinConnection(int minConnection) {
        this.minConnection = minConnection;
    }

    public int getMinConnection() {
        return minConnection;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getBasePackage() {
        return basePackage;
    }
}
