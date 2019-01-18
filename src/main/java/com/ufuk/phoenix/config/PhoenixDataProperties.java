package com.ufuk.phoenix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("phoenix.data")
public class PhoenixDataProperties {

    private String jdbcUrl;
    private int minConnection;
    private int maxConnection;

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
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
}
