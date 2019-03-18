package io.github.ufukhalis.phoenix.query;

import io.github.ufukhalis.phoenix.data.PhoenixCrudRepository;
import io.github.ufukhalis.phoenix.mapper.QueryResolver;

import java.util.List;


public final class PhoenixCursor<T> {

    private String cursorName;
    private PhoenixQuery phoenixQuery;
    private PhoenixCrudRepository<T, ?> phoenixCrudRepository;

    public PhoenixCursor(String cursorName, PhoenixQuery phoenixQuery, PhoenixCrudRepository<T, ?> phoenixCrudRepository) {
        this.phoenixCrudRepository = phoenixCrudRepository;
        this.cursorName = cursorName;
        this.phoenixQuery = phoenixQuery;
    }

    public void declareAndOpenCursor() {
        final String sqlDeclareCursor = QueryResolver.toDeclareCursor(this.cursorName, this.phoenixQuery.sql());
        final String sqlOpenCursor = QueryResolver.toOpenCursor(this.cursorName);

        phoenixCrudRepository.executeUpdate(sqlDeclareCursor);
        phoenixCrudRepository.executeUpdate(sqlOpenCursor);
    }

    public List<T> fetch(int limit) {
        final String sqlFetchFromCursor = QueryResolver.toFetchFromCursor(this.cursorName, limit);
        return phoenixCrudRepository.find(sqlFetchFromCursor, this.phoenixQuery.fields());
    }


    public void closeCursor() {
        final String sqlCloseCursor = QueryResolver.toCloseCursor(this.cursorName);

        phoenixCrudRepository.executeUpdate(sqlCloseCursor);
    }
}
