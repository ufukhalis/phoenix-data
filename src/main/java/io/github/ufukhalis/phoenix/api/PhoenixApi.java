package io.github.ufukhalis.phoenix.api;

public interface PhoenixApi {

    <T> T save(T entity);

    <T> Iterable<T> save(Iterable<T> entities);

}
