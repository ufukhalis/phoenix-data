package io.github.ufukhalis.phoenix.config;

import io.github.ufukhalis.phoenix.data.PhoenixRepository;
import io.github.ufukhalis.phoenix.mapper.AnnotationResolver;
import io.github.ufukhalis.phoenix.mapper.Entity;
import io.github.ufukhalis.phoenix.mapper.QueryResolver;
import org.atteo.classindex.ClassIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class PhoenixApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PhoenixRepository phoenixRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        final Iterable<Class<?>> classes = ClassIndex.getAnnotated(Entity.class);

        new AnnotationResolver()
                .resolve(classes)
                .map(QueryResolver::toCreateTable)
                .map(rawSql -> phoenixRepository.executeQuery(rawSql));
    }
}
