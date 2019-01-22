package io.github.ufukhalis.phoenix.data;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;


public class PhoenixDataConnection {

    private final Logger logger = LoggerFactory.getLogger(PhoenixDataConnection.class);

    private final String CLASS_NAME = "org.apache.phoenix.queryserver.client.Driver";

    private String jdbcUrl;
    private Option<Integer> maxConnection;
    private Option<Integer> minConnection;
    private Connection connection;

    public PhoenixDataConnection(String jdbcUrl, int maxConnection, int minConnection) {
        this.jdbcUrl = jdbcUrl;
        this.maxConnection = Option.some(maxConnection);
        this.minConnection = Option.some(minConnection);
    }

    public Connection connect() {
        this.connection =  Try.of(() -> {
            logger.debug("Creating connection..");

            Class.forName(CLASS_NAME);
            final Connection connection = DriverManager.getConnection(jdbcUrl);
            connection.setAutoCommit(true);
            return connection;
        }).getOrElseThrow(e -> new RuntimeException("Couldn't connect to Phoenix server", e));
        return connection;
    }

    public Option<Integer> getMaxConnection() {
        return maxConnection;
    }

    public Option<Integer> getMinConnection() {
        return minConnection;
    }
}
