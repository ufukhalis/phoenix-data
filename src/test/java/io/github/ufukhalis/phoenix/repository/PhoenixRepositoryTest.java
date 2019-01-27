package io.github.ufukhalis.phoenix.repository;


import io.github.ufukhalis.phoenix.config.PhoenixDataConfig;
import io.github.ufukhalis.phoenix.data.PhoenixConnectionPool;
import io.github.ufukhalis.phoenix.data.PhoenixRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PhoenixRepository.class, PhoenixConnectionPool.class, PhoenixDataConfig.class})
public class PhoenixRepositoryTest {

    @Autowired
    PhoenixRepository phoenixRepository;

    @MockBean
    PhoenixConnectionPool phoenixConnectionPool;

    @Mock
    Connection connection;

    @Mock
    Statement statement;

    @Mock
    PreparedStatement preparedStatement;

    @Mock
    ResultSet resultSet;

    @Before
    public void setup() throws SQLException {
        when(phoenixConnectionPool.getConnectionFromPool()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(statement.executeUpdate(anyString())).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(phoenixConnectionPool).releaseConnection(any());
    }

    @Test
    public void test_given_sql_executeUpdate_should_return_valid_value() {
        Assert.assertEquals(1, phoenixRepository.executeUpdate("drop database test_db"));
    }

    @Test
    public void test_given_sql_executeQuery_should_return_resultSet() {
        Assert.assertTrue(phoenixRepository.executeQuery("select * from table_test") != null);
    }
}
