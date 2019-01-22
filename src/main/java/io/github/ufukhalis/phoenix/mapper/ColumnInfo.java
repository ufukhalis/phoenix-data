package io.github.ufukhalis.phoenix.mapper;

import io.vavr.control.Option;

import java.io.Serializable;

public class ColumnInfo implements Serializable {

    private String columnName;
    private Class<?> columnClass;
    private Option<Object> columnValue;
    private boolean isPrimaryKey;

    public ColumnInfo(String columnName, Class<?> columnClass, Option<Object> columnValue, boolean isPrimaryKey) {
        this.columnName = columnName;
        this.columnClass = columnClass;
        this.columnValue = columnValue;
        this.isPrimaryKey = isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setColumnClass(Class<?> columnClass) {
        this.columnClass = columnClass;
    }

    public Class<?> getColumnClass() {
        return columnClass;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnValue(Option<Object> columnValue) {
        this.columnValue = columnValue;
    }

    public Option<Object> getColumnValue() {
        return columnValue;
    }
}
