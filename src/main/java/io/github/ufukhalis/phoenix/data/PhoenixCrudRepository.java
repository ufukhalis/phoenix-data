package io.github.ufukhalis.phoenix.data;

public interface PhoenixCrudRepository<T> {

    T save(T entity);

    Iterable<T> save(Iterable<T> entities);

}
