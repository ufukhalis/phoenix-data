package io.github.ufukhalis.phoenix.util;

import java.util.function.Predicate;

public final class Predicates {

    public static Predicate<Class<?>> isInstanceOfString = c -> c.equals(String.class);
    public static Predicate<Class<?>> isInstanceOfInteger = c -> c.equals(Integer.class) || c.equals(int.class);


}
