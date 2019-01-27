package io.github.ufukhalis.phoenix.repository;

import io.github.ufukhalis.phoenix.config.PhoenixDataConfig;
import io.github.ufukhalis.phoenix.data.PhoenixConnectionPool;
import io.github.ufukhalis.phoenix.data.PhoenixRepository;
import io.github.ufukhalis.phoenix.model.TestEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PhoenixRepository.class, PhoenixConnectionPool.class, PhoenixDataConfig.class, TestRepository.class})
public class PhoenixCrudRepositoryTest {

    @MockBean
    PhoenixRepository phoenixRepository;

    @Autowired
    TestRepository testRepository;

    @Mock
    ResultSet resultSet;

    @Before
    public void setup() {
        when(phoenixRepository.executeUpdate(anyString())).thenReturn(1);
        when(phoenixRepository.executeQuery(anyString())).thenReturn(resultSet);
    }

    @Test
    public void test_given_any_valid_sql_execute_update_should_return_result() {
        final String sql = "drop database test_db";

        Assert.assertEquals(1, testRepository.executeUpdate(sql));
    }

    @Test
    public void test_given_any_valid_sql_execute_query_should_return_result() {
        final String sql = "select * from test_table";

        Assert.assertNotNull(testRepository.executeQuery(sql));
    }

    @Test
    public void test_given_any_valid_sql_execute_update_async_should_return_result() {
        final String sql = "drop database test_db";

        Assert.assertEquals(1, testRepository.executeUpdateAsync(sql).get().intValue());
    }

    @Test
    public void test_given_any_valid_sql_execute_query_async_should_return_result() {
        final String sql = "select * from test_table";

        Assert.assertNotNull(testRepository.executeQueryAsync(sql).get());
    }

    @Test
    public void test_given_entity_save_should_return_same_entity() {
        final TestEntity testEntity = buildMockEntity();
        Assert.assertEquals(testEntity, testRepository.save(testEntity));
    }

    @Test
    public void test_given_entities_saveAll_should_return_same_entities() {
        final List<TestEntity> entities = io.vavr.collection.List.of(buildMockEntity()).asJava();

        Assert.assertEquals(entities, testRepository.save(entities));
    }

    @Test
    public void test_given_primary_key_delete_should_return_result() {

        Assert.assertEquals(1, testRepository.delete(1));
    }

    @Test
    public void test_given_primary_key_find_should_return_entity() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("phoenix");
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        Assert.assertTrue(testRepository.find(1).isPresent());
        Assert.assertTrue(testRepository.findAll().isEmpty());
    }

    @Test
    public void test_findAll_should_not_return_empty_list() throws SQLException {
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("phoenix");
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        Assert.assertTrue(!testRepository.findAll().isEmpty());
    }

    private static TestEntity buildMockEntity() {
        final TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setName("phoenix");

        return testEntity;
    }
}

