package io.github.ufukhalis.phoenix.mapper;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class AnnotationResolver {

    public EntityInfo resolve(Object object) {
        final Class<?> objectClass = object.getClass();

        if (objectClass.isAnnotationPresent(Entity.class)) {
            return resolveClass(objectClass, Option.of(object));
        } else {
            throw new RuntimeException("This class has no @Entity annotation");
        }
    }

    public List<EntityInfo> resolve(Iterable<Class<?>> classes) {
        return List.ofAll(classes).map(c -> resolveClass(c, Option.none()));
    }

    private EntityInfo resolveClass(Class<?> objectClass, Option<Object> maybeObject) {
        final String entityName = objectClass.getAnnotation(Entity.class).value();

        final List<ColumnInfo> columnInfoList =
                List.of(objectClass.getDeclaredFields())
                        .filter(field -> {
                            field.setAccessible(true);
                            return field.isAnnotationPresent(Column.class);
                        }).map(field -> {
                            final String columnName = field.getAnnotation(Column.class).value();
                            final Class columnClass = field.getDeclaringClass();
                            final Object object = maybeObject.map(obj -> Try.of(() -> field.get(obj))
                                    .getOrElseThrow(e -> new RuntimeException("Column value couldn't read", e)))
                                    .get();
                            return new ColumnInfo(columnName, columnClass, object);
                        }
                );

        return new EntityInfo(entityName, columnInfoList);
    }
}
