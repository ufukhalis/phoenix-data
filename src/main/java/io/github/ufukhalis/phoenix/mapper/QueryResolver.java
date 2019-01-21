package io.github.ufukhalis.phoenix.mapper;

import static io.github.ufukhalis.phoenix.util.Predicates.*;
import static io.vavr.API.*;

public final class QueryResolver {

    private static final String HOLDER_TABLE_NAME = "$table_name";
    private static final String HOLDER_COLUMN_DETAILS = "$column_details";

    private static final String RAW_CREATE_TABLE_QUERY =
            "create table if not exists " + HOLDER_TABLE_NAME + " (" + HOLDER_COLUMN_DETAILS + ")";

    private static final String RAW_DROP_TABLE_QUERY =
            "drop table if exists " + HOLDER_TABLE_NAME;

    public static String toCreateTable(EntityInfo entityInfo) {
        final String tableName = entityInfo.getTableName();

        final String columnDetails = entityInfo.getColumnInfo()
                .map(column -> column.getColumnName() + resolveTypeForSQL(column.getColumnClass()) + primaryKey(column.isPrimaryKey()))
                .intersperse(", ")
                .foldLeft(new StringBuilder() , StringBuilder::append).toString();

        return RAW_CREATE_TABLE_QUERY
                .replace(HOLDER_TABLE_NAME, tableName)
                .replace(HOLDER_COLUMN_DETAILS, columnDetails);
    }

    public static String toDropTable(EntityInfo entityInfo) {
        return RAW_DROP_TABLE_QUERY.replace(HOLDER_TABLE_NAME, entityInfo.getTableName());
    }

    private static String resolveTypeForSQL(Class<?> fieldClass) {
        return Match(fieldClass).of(
                Case($(isInstanceOfString), () ->  " varchar"),
                Case($(isInstanceOfInteger), () ->  " integer"),
                Case($(isInstanceOfDouble), () ->  " double"),
                Case($(isInstanceOfFloat), () ->  " float"),
                Case($(isInstanceOfLong), () ->  " bigint"),
                Case($(isInstanceOfBoolean), () ->  " tinyint"),
                Case($(), o -> { throw new RuntimeException("Field class type not found, please give valid field class type."); })
        );
    }

    private static String primaryKey(boolean isPrimaryKey) {
        return Match(isPrimaryKey).of(
                Case($(true), " not null primary key"),
                Case($(), "")
        );
    }
}
