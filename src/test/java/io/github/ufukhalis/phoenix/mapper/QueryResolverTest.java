package io.github.ufukhalis.phoenix.mapper;

import io.github.ufukhalis.phoenix.model.TestEntity;
import org.junit.Assert;
import org.junit.Test;

public class QueryResolverTest {

    private AnnotationResolver annotationResolver = new AnnotationResolver();

    @Test
    public void test_given_entity_should_return_create_sql() {
        final String expected = "create table if not exists tableName (id bigint not null primary key, name varchar)";
        final String actual = QueryResolver.toCreateTable(prepareEntity());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_given_entity_should_return_save_sql() {
        final String expected = "upsert into tableName (id,name) VALUES (1,'phoenix')";
        final String actual = QueryResolver.toSaveEntity(prepareEntity());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_given_entity_should_return_findAll_sql() {
        final String expected = "select * from tableName";
        final String actual = QueryResolver.toFindAll(prepareEntity());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_given_entity_should_return_drop_sql() {
        final String expected = "drop table if exists tableName";
        final String actual = QueryResolver.toDropTable(prepareEntity());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_given_entity_should_return_find_sql() {
        final String expected = "select * from tableName where id=1 limit 1";
        final String actual = QueryResolver.toFind(prepareEntity(), 1L);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_given_entity_should_return_delete_sql() {
        final String expected = "delete from tableName where id=1";
        final String actual = QueryResolver.toDelete(prepareEntity(), 1L);

        Assert.assertEquals(expected, actual);
    }

    private EntityInfo prepareEntity() {
        final TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setName("phoenix");

        return annotationResolver.resolve(testEntity);
    }
}
