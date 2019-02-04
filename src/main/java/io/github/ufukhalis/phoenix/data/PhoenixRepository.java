package io.github.ufukhalis.phoenix.data;

import io.github.ufukhalis.phoenix.config.PhoenixDataProperties;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;

@Repository
@EnableConfigurationProperties(PhoenixDataProperties.class)
public class PhoenixRepository {

    private final Logger logger = LoggerFactory.getLogger(PhoenixRepository.class);

    private PhoenixConnectionPool phoenixConnectionPool;

    public PhoenixRepository(PhoenixConnectionPool phoenixConnectionPool) {
        this.phoenixConnectionPool = phoenixConnectionPool;
    }

    public int executeUpdate(final String sql) {
        logger.debug("Executing query {}", sql);

        final Connection connection = phoenixConnectionPool.getConnectionFromPool();

        return Try.withResources(connection::createStatement)
                .of(statement -> statement.executeUpdate(sql))
                .andFinally(() -> phoenixConnectionPool.releaseConnection(Option.of(connection)))
                .getOrElseThrow(e -> new RuntimeException("PhoenixQuery execution failed", e));
    }

    public ResultSet executeQuery(final String sql) {
        logger.debug("Executing query for result set {}", sql);

        final Connection connection = phoenixConnectionPool.getConnectionFromPool();

        return Try.of(() -> connection.prepareStatement(sql))
                .map(preparedStatement ->
                        Try.of(preparedStatement::executeQuery)
                        .getOrElseThrow(e -> new RuntimeException("PhoenixQuery execution failed", e)))
                .andFinally(() -> phoenixConnectionPool.releaseConnection(Option.of(connection)))
        .getOrElseThrow(e -> new RuntimeException("PhoenixQuery execution failed", e));

    }

}
