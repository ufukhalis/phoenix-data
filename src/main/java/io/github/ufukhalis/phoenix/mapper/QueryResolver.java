package io.github.ufukhalis.phoenix.mapper;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import static io.github.ufukhalis.phoenix.util.Predicates.*;
import static io.vavr.API.*;

public final class QueryResolver {

    private static final String HOLDER_TABLE_NAME = "$table_name";
    private static final String HOLDER_COLUMN_DETAILS = "$column_details";
    private static final String HOLDER_COLUMN_VALUES = "$column_values";
    private static final String HOLDER_CONDITIONS = "$conditions";
    private static final String HOLDER_FIELDS = "$fields";

    private static final String RAW_CREATE_TABLE_QUERY =
            "create table if not exists " + HOLDER_TABLE_NAME + " (" + HOLDER_COLUMN_DETAILS + ")";

    private static final String RAW_DROP_TABLE_QUERY =
            "drop table if exists " + HOLDER_TABLE_NAME;

    private static final String RAW_UPSERT_TABLE_QUERY =
            "upsert into " + HOLDER_TABLE_NAME + " (" + HOLDER_COLUMN_DETAILS + ") VALUES (" + HOLDER_COLUMN_VALUES + ")";

    private static final String RAW_FIND_ONE_QUERY =
            "select * from " + HOLDER_TABLE_NAME + " where " + HOLDER_CONDITIONS + " limit 1";

    private static final String RAW_SELECT_FROM_QUERY =
            "select " + HOLDER_FIELDS + " from " + HOLDER_TABLE_NAME;

    private static final String RAW_FIND_ALL_QUERY =
            "select * from " + HOLDER_TABLE_NAME;

    private static final String RAW_DELETE_QUERY =
            "delete from " + HOLDER_TABLE_NAME + " where " + HOLDER_CONDITIONS;

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

    public static String toSaveEntity(EntityInfo entityInfo) {
        final String tableName = entityInfo.getTableName();

        final List<ColumnInfo> definedColumns = entityInfo.getColumnInfo()
                .filter(columnInfo -> columnInfo.getColumnValue().isDefined());

        final String columnDetails = definedColumns
                .map(ColumnInfo::getColumnName)
                .mkString(",");

        final String columnValues = definedColumns
                .map(columnInfo -> resolveTypeForSQLValue(columnInfo.getColumnValue().get()))
                .mkString(",");

        return RAW_UPSERT_TABLE_QUERY
                .replace(HOLDER_TABLE_NAME, tableName)
                .replace(HOLDER_COLUMN_DETAILS, columnDetails)
                .replace(HOLDER_COLUMN_VALUES, columnValues);
    }

    public static String toFind(EntityInfo entityInfo, Object primaryKeyValue) {
        final Tuple2<String, String> query = prepareForQuery(entityInfo, primaryKeyValue);

        return RAW_FIND_ONE_QUERY
                .replace(HOLDER_TABLE_NAME, query._1)
                .replace(HOLDER_CONDITIONS, query._2);
    }

    public static String toSelectFrom(EntityInfo entityInfo, String ...fields) {
        return RAW_SELECT_FROM_QUERY
                .replace(HOLDER_FIELDS, fields.length > 0 ? List(fields).mkString(",") : "*")
                .replace(HOLDER_TABLE_NAME, entityInfo.getTableName());
    }

    public static String toFindAll(EntityInfo entityInfo) {
        return RAW_FIND_ALL_QUERY
                .replace(HOLDER_TABLE_NAME, entityInfo.getTableName());
    }

    public static String toDelete(EntityInfo entityInfo, Object primaryKeyValue) {
        final Tuple2<String, String> query = prepareForQuery(entityInfo, primaryKeyValue);

        return RAW_DELETE_QUERY
                .replace(HOLDER_TABLE_NAME, query._1)
                .replace(HOLDER_CONDITIONS, query._2);
    }

    private static String resolveTypeForSQL(Class<?> fieldClass) {
        return Match(fieldClass).of(
                Case($(isInstanceOfString), () ->  " varchar"),
                Case($(isInstanceOfInteger), () ->  " integer"),
                Case($(isInstanceOfDouble), () ->  " double"),
                Case($(isInstanceOfFloat), () ->  " float"),
                Case($(isInstanceOfLong), () ->  " bigint"),
                Case($(isInstanceOfBoolean), () ->  " boolean"),
                Case($(), o -> { throw new RuntimeException("Field class type not found, please give valid field class type."); })
        );
    }

    public static String resolveTypeForSQLValue(Object object) {
        final Class<?> objectClass = object.getClass();

        return Match(objectClass).of(
                Case($(isInstanceOfString), () -> "'"  + object + "'"),
                Case($(), () -> object + "")
        );
    }

    private static String primaryKey(boolean isPrimaryKey) {
        return Match(isPrimaryKey).of(
                Case($(true), " not null primary key"),
                Case($(), "")
        );
    }

    private static Tuple2<String, String> prepareForQuery(EntityInfo entityInfo, Object primaryKeyValue) {
        final String tableName = entityInfo.getTableName();

        final ColumnInfo primaryKeyColumn = entityInfo.getColumnInfo().filter(ColumnInfo::isPrimaryKey).get();

        final String conditions = primaryKeyColumn.getColumnName() + "=" + resolveTypeForSQLValue(primaryKeyValue);

        return Tuple.of(tableName, conditions);
    }
}
