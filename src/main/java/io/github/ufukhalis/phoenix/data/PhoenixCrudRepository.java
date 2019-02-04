package io.github.ufukhalis.phoenix.data;

import io.github.ufukhalis.phoenix.mapper.AnnotationResolver;
import io.github.ufukhalis.phoenix.mapper.Column;
import io.github.ufukhalis.phoenix.mapper.EntityInfo;
import io.github.ufukhalis.phoenix.mapper.QueryResolver;
import io.github.ufukhalis.phoenix.query.PhoenixQuery;
import io.github.ufukhalis.phoenix.util.Predicates;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;

import static io.vavr.API.*;

@Repository
public abstract class PhoenixCrudRepository <T, ID> {

    @Autowired
    private PhoenixRepository phoenixRepository;

    private AnnotationResolver annotationResolver = new AnnotationResolver();

    private Class<T> entityClass;

    @PostConstruct
    public void init() {
        this.entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T save(T entity) {
        final EntityInfo entityInfo = annotationResolver.resolve(entity);
        final String upsertSql = QueryResolver.toSaveEntity(entityInfo);

        phoenixRepository.executeUpdate(upsertSql);

        return entity;
    }

    public Iterable<T> save(Iterable<T> entities) {
        final List<EntityInfo> entityInfoList = List.ofAll(entities)
                .map(entity -> annotationResolver.resolve(entity));

        entityInfoList.map(QueryResolver::toSaveEntity)
                .map(sql -> phoenixRepository.executeUpdate(sql));
        return entities;
    }

    public Optional<T> find(ID primaryKey) {
        final EntityInfo entityInfo = annotationResolver.resolveClass(entityClass, Option.none());

        final String rawSql = QueryResolver.toFind(entityInfo, primaryKey);

        final ResultSet resultSet = phoenixRepository.executeQuery(rawSql);

        return Try.of(() -> findAll(resultSet).headOption().toJavaOptional())
                .getOrElseThrow(e -> new RuntimeException("Entity find exception", e));
    }

    public java.util.List<T> find(PhoenixQuery phoenixQuery) {
        if (!phoenixQuery.entityClass().equals(this.entityClass)) {
            throw new RuntimeException("Entity class couldn't match");
        }

        final ResultSet resultSet = phoenixRepository.executeQuery(phoenixQuery.sql());

        return Try.of(() -> findAll(resultSet, phoenixQuery.fields()).asJava())
                .getOrElseThrow(e -> new RuntimeException("Entity find all exceptions", e));
    }

    public java.util.List<T> findAll() {
        final EntityInfo entityInfo = annotationResolver.resolveClass(entityClass, Option.none());

        final String rawSql = QueryResolver.toFindAll(entityInfo);

        final ResultSet resultSet = phoenixRepository.executeQuery(rawSql);

        return Try.of(() -> findAll(resultSet).asJava())
                .getOrElseThrow(e -> new RuntimeException("Entity find all exceptions", e));
    }

    public int delete(ID primaryKey) {
        final EntityInfo entityInfo = annotationResolver.resolveClass(entityClass, Option.none());

        final String rawSql = QueryResolver.toDelete(entityInfo, primaryKey);

        return phoenixRepository.executeUpdate(rawSql);
    }

    public int executeUpdate(String sql) {
        return phoenixRepository.executeUpdate(sql);
    }

    public ResultSet executeQuery(String sql) {
        return phoenixRepository.executeQuery(sql);
    }

    public Future<Integer> executeUpdateAsync(String sql) {
        return Future.of(() -> executeUpdate(sql));
    }

    public Future<ResultSet> executeQueryAsync(String sql) {
        return Future.of(() -> executeQuery(sql));
    }

    private List<T> findAll(ResultSet resultSet, String ...fields) throws Exception {
        final java.util.List<T> entities = new ArrayList<>();
        final Set<String> fieldSet = Set(fields);

        while (resultSet.next()) {
            final T entity = entityClass.getConstructor().newInstance();

            List.of(entityClass.getDeclaredFields())
                    .filter(field -> !field.getName().contains("jacoco"))
                    .filter(field -> !fieldSet.isEmpty() && fieldSet.contains(field.getName()))
                    .forEach(field -> {
                        Column column = field.getAnnotation(Column.class);
                        final Object object = getValueFromResultSet(field.getType(), column.value(), resultSet);
                        field.setAccessible(true);
                        Try.run(() -> field.set(entity, object))
                                .getOrElseThrow(e -> new RuntimeException("Field is not accessible", e));
                    });
            entities.add(entity);
        }
        return List.ofAll(entities);
    }

    private Object getValueFromResultSet(Class<?> fieldClass, String columnName, ResultSet rs) {
        return Match(fieldClass).of(
                Case($(Predicates.isInstanceOfString), () -> Try.of(() -> rs.getString(columnName)).getOrElseThrow(e -> new RuntimeException("String casting error", e))),
                Case($(Predicates.isInstanceOfInteger),() ->  Try.of(() -> rs.getInt(columnName)).getOrElseThrow(e -> new RuntimeException("Integer casting error", e))),
                Case($(Predicates.isInstanceOfLong), () ->  Try.of(() -> rs.getLong(columnName)).getOrElseThrow(e -> new RuntimeException("Long casting error", e))),
                Case($(Predicates.isInstanceOfDouble), () ->  Try.of(() -> rs.getDouble(columnName)).getOrElseThrow(e -> new RuntimeException("Double casting error", e))),
                Case($(Predicates.isInstanceOfFloat), () ->  Try.of(() -> rs.getFloat(columnName)).getOrElseThrow(e -> new RuntimeException("Float casting error", e))),
                Case($(Predicates.isInstanceOfBoolean), () ->  Try.of(() -> rs.getBoolean(columnName)).getOrElseThrow(e -> new RuntimeException("Boolean casting error", e))),
                Case($(), () ->  new RuntimeException("Class type couldn't found"))
        );
    }
}
