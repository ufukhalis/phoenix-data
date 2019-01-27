package io.github.ufukhalis.phoenix.mapper;

import io.github.ufukhalis.phoenix.model.TestEntity;
import io.github.ufukhalis.phoenix.model.TestModel;
import io.github.ufukhalis.phoenix.util.PackageUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class AnnotationResolverTest {

    private final AnnotationResolver annotationResolver = new AnnotationResolver();

    @Test
    public void test_given_class_list_should_be_resolve_correctly() {
        final List<Class<?>> classList = PackageUtil.find("io.github.ufukhalis")
                .stream()
                .filter(c -> c.isAnnotationPresent(Entity.class))
                .collect(Collectors.toList());

        Assert.assertTrue(!annotationResolver.resolve(classList).isEmpty());
    }

    @Test
    public void test_given_entity_should_be_resolve_correctly() {
        final TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setName("phoenix");

        EntityInfo entityInfo = annotationResolver.resolve(testEntity);

        Assert.assertEquals("tableName", entityInfo.getTableName());
    }

    @Test(expected = RuntimeException.class)
    public void test_given_entity_has_no_annotation_throw_exception() {
        final TestModel testModel = new TestModel();
        testModel.setId(1L);
        testModel.setName("phoenix");

        annotationResolver.resolve(testModel);
    }
}
