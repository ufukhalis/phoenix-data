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
import java.sql.PreparedStatement;
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
                .getOrElseThrow(e -> new RuntimeException("Query execution failed", e));
    }

    public ResultSet executeQuery(final String sql) {
        logger.debug("Executing query for result set {}", sql);

        final Connection connection = phoenixConnectionPool.getConnectionFromPool();

        return Try.withResources(() -> connection.prepareStatement(sql))
                .of(PreparedStatement::executeQuery)
                .andFinally(() -> phoenixConnectionPool.releaseConnection(Option.of(connection)))
                .getOrElseThrow(e -> new RuntimeException("Query execution failed", e));

    }

    public Future<Integer> executeUpdateAsync(final String sql) {
        return Future.of(() -> executeUpdate(sql));
    }

    public Future<ResultSet> executeQueryAsync(final String sql) {
        return Future.of(() -> executeQuery(sql));
    }

}
