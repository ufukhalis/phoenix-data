package io.github.ufukhalis.phoenix.query;

import io.github.ufukhalis.phoenix.model.TestEntity;
import org.junit.Assert;
import org.junit.Test;


public class PhoenixQueryTest {

    @Test
    public void test_created_query_should_match() {
        final PhoenixQuery query = new PhoenixQuery.Builder(TestEntity.class)
                .select("id","name")
                .where()
                .condition("name", "phoenix", PhoenixQuery.Operator.EQUAL)
                .or()
                .condition("id", 1, PhoenixQuery.Operator.GT)
                .or()
                .condition("id", 1, PhoenixQuery.Operator.GTE)
                .or()
                .condition("id", 1, PhoenixQuery.Operator.LT)
                .or()
                .condition("id", 1, PhoenixQuery.Operator.LTE)
                .and()
                .condition("name", "data", PhoenixQuery.Operator.NOT_EQUAL)
                .limit(10)
                .build();

        Assert.assertEquals("select id,name from tableName where name='phoenix' or id>1 or id>=1 or id<1 or id<=1 and name!='data' limit 10",
                query.sql());
    }

    @Test
    public void test_created_query_entity_class_should_match() {
        final PhoenixQuery query = new PhoenixQuery.Builder(TestEntity.class)
                .select("id","name").build();

        Assert.assertEquals(TestEntity.class, query.entityClass());
    }
}
