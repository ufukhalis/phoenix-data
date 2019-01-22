package io.github.ufukhalis.phoenix.util;

import org.junit.Assert;
import org.junit.Test;

public class PredicatesTest {

    @Test
    public void test_given_class_should_be_string() {
        Assert.assertTrue(Predicates.isInstanceOfString.test(String.class));
    }

    @Test
    public void test_given_class_should_be_integer() {
        Assert.assertTrue(Predicates.isInstanceOfInteger.test(Integer.class));
    }

    @Test
    public void test_given_class_should_be_long() {
        Assert.assertTrue(Predicates.isInstanceOfLong.test(Long.class));
    }

    @Test
    public void test_given_class_should_be_float() {
        Assert.assertTrue(Predicates.isInstanceOfFloat.test(Float.class));
    }

    @Test
    public void test_given_class_should_be_double() {
        Assert.assertTrue(Predicates.isInstanceOfDouble.test(Double.class));
    }

    @Test
    public void test_given_class_should_be_boolean() {
        Assert.assertTrue(Predicates.isInstanceOfBoolean.test(Boolean.class));
    }
}
