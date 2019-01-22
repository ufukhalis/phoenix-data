package io.github.ufukhalis.phoenix.config;

import io.github.ufukhalis.phoenix.data.PhoenixRepository;
import io.github.ufukhalis.phoenix.mapper.*;
import io.github.ufukhalis.phoenix.util.PackageUtil;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import static io.vavr.API.*;

import java.util.stream.Collectors;

@Component
public class PhoenixApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PhoenixRepository phoenixRepository;

    @Autowired
    private PhoenixDataProperties phoenixDataProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        final Iterable<Class<?>> classes = PackageUtil.find(phoenixDataProperties.getBasePackage())
                .stream()
                .filter(c -> c.isAnnotationPresent(Entity.class))
                .collect(Collectors.toList());

        final List<EntityInfo> resolvedClasses = new AnnotationResolver().resolve(classes);

        final TableStrategy tableStrategy = Try.of(() -> TableStrategy.valueOf(phoenixDataProperties.getTableStrategy()))
                .getOrElseThrow(e -> new RuntimeException("Strategy is not valid", e));

        Match(tableStrategy).of(
                Case($(TableStrategy.CREATE), () ->
                        resolvedClasses.map(QueryResolver::toCreateTable)
                                .map(phoenixRepository::executeUpdate)),
                Case($(TableStrategy.DROP_CREATE), () -> {
                    resolvedClasses.map(QueryResolver::toDropTable).map(phoenixRepository::executeUpdate);
                    return resolvedClasses.map(QueryResolver::toCreateTable).map(phoenixRepository::executeUpdate);
                }),
                Case($(TableStrategy.NONE), () -> ""),
                Case($(), () -> new RuntimeException("Strategy is not valid"))
        );
    }
}
