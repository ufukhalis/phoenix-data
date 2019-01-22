package io.github.ufukhalis.phoenix.data;

import io.github.ufukhalis.phoenix.mapper.AnnotationResolver;
import io.github.ufukhalis.phoenix.mapper.EntityInfo;
import io.github.ufukhalis.phoenix.mapper.QueryResolver;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;

@Repository
public abstract class PhoenixCrudRepository <T, ID> {

    @Autowired
    private PhoenixRepository phoenixRepository;

    private Class<T> entityClass;
    private Class<ID> primaryKeyClass;

    @PostConstruct
    public void init() {
        this.entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.primaryKeyClass = (Class<ID>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public T save(T entity) {
        final EntityInfo entityInfo = new AnnotationResolver().resolve(entity);
        final String upsertSql = QueryResolver.toSaveEntity(entityInfo);

        phoenixRepository.executeUpdate(upsertSql);

        return entity;
    }

    public Iterable<T> save(Iterable<T> entities) {
        final List<EntityInfo> entityInfoList = List.ofAll(entities)
                .map(entity -> new AnnotationResolver().resolve(entity));

        entityInfoList.map(QueryResolver::toSaveEntity)
                .map(sql -> phoenixRepository.executeUpdate(sql));
        return entities;
    }

    public T find(ID primaryKey) {
        final EntityInfo entityInfo = new AnnotationResolver().resolveClass(entityClass, Option.none());

        final String rawSql = QueryResolver.toFind(entityInfo, primaryKey.toString());

        final ResultSet resultSet = phoenixRepository.executeQuery(rawSql);
        try {
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
//
//        Try.of(() -> entityClass.getConstructor().newInstance())
//                .map(entity -> {
//                   entity.getClass().getDeclaredFields();
//                   resultSet.get
//                });

        throw new RuntimeException("Not implemented yet");
    }
}
