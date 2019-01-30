package io.github.ufukhalis.phoenix.config;

import io.github.ufukhalis.phoenix.data.PhoenixConnectionPool;
import io.github.ufukhalis.phoenix.data.PhoenixRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PhoenixRepository.class, PhoenixConnectionPool.class, PhoenixDataConfig.class, PhoenixApplicationListener.class})
public class PhoenixApplicationListenerTest {

    @Autowired
    PhoenixApplicationListener phoenixApplicationListener;

    @Test
    public void test_onApplicationEvent_should_not_throw_exception() {
        phoenixApplicationListener.onApplicationEvent(null);
    }
}
