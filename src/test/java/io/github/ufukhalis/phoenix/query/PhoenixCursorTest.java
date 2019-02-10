package io.github.ufukhalis.phoenix.query;

import io.github.ufukhalis.phoenix.config.PhoenixDataConfig;
import io.github.ufukhalis.phoenix.data.PhoenixConnectionPool;
import io.github.ufukhalis.phoenix.data.PhoenixRepository;
import io.github.ufukhalis.phoenix.model.TestEntity;
import io.github.ufukhalis.phoenix.repository.TestRepository;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PhoenixRepository.class, PhoenixConnectionPool.class, PhoenixDataConfig.class, TestRepository.class})
public class PhoenixCursorTest {

    @MockBean
    PhoenixRepository phoenixRepository;

    @Autowired
    TestRepository testRepository;

    @Mock
    ResultSet resultSet;

    @Before
    public void setup() throws SQLException {
        when(phoenixRepository.executeUpdate(anyString())).thenReturn(1);
        when(phoenixRepository.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("phoenix");
        when(resultSet.next()).thenReturn(true).thenReturn(false);

    }

    @Test
    public void test_fetch_should_return_one_size_element() {
        final PhoenixQuery phoenixQuery = new PhoenixQuery.Builder(TestEntity.class)
                .select().build();

        final PhoenixCursor<TestEntity> cursor = new PhoenixCursor<>("cursorName", phoenixQuery, testRepository);

        cursor.declareAndOpenCursor();

        Assert.assertEquals(1, cursor.fetch(10).size());

        cursor.closeCursor();
    }
}
