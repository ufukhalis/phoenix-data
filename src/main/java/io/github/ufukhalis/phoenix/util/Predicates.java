package io.github.ufukhalis.phoenix.util;

import java.util.function.Predicate;

public final class Predicates {

    public static Predicate<Class<?>> isInstanceOfString = c -> c.equals(String.class);
    public static Predicate<Class<?>> isInstanceOfInteger = c -> c.equals(Integer.class) || c.equals(int.class);
    public static Predicate<Class<?>> isInstanceOfDouble = c -> c.equals(Double.class) || c.equals(double.class);
    public static Predicate<Class<?>> isInstanceOfFloat = c -> c.equals(Float.class) || c.equals(float.class);
    public static Predicate<Class<?>> isInstanceOfLong = c -> c.equals(Long.class) || c.equals(long.class);
    public static Predicate<Class<?>> isInstanceOfBoolean = c -> c.equals(Boolean.class) || c.equals(boolean.class);

}
