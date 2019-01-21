package io.github.ufukhalis.phoenix.data;

import io.github.ufukhalis.phoenix.config.PhoenixDataProperties;
import io.github.ufukhalis.phoenix.mapper.AnnotationResolver;
import io.github.ufukhalis.phoenix.mapper.EntityInfo;
import io.github.ufukhalis.phoenix.mapper.QueryResolver;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
@EnableConfigurationProperties(PhoenixDataProperties.class)
public class PhoenixRepository <T, ID> {

    private final Logger logger = LoggerFactory.getLogger(PhoenixRepository.class);

    private PhoenixConnectionPool phoenixConnectionPool;

    private Class<T> entityClass;
    private Class<ID> primaryKeyClass;

    public PhoenixRepository(PhoenixConnectionPool phoenixConnectionPool) {
        this.phoenixConnectionPool = phoenixConnectionPool;
        this.entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.primaryKeyClass = (Class<ID>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
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

    public T save(T entity) {
        final EntityInfo entityInfo = new AnnotationResolver().resolve(entity);
        final String upsertSql = QueryResolver.toCreateTable(entityInfo);

        executeUpdate(upsertSql);

        return entity;
    }

    public Iterable<T> save(Iterable<T> entities) {
        final List<EntityInfo> entityInfoList = List.ofAll(entities)
                .map(entity -> new AnnotationResolver().resolve(entity));

        entityInfoList.map(QueryResolver::toCreateTable)
                .map(this::executeUpdate);
        return entities;
    }

    public T find(ID primaryKey) {
        final EntityInfo entityInfo = new AnnotationResolver().resolveClass(entityClass, Option.none());
        throw new RuntimeException("Not implemented yet");
    }
}
