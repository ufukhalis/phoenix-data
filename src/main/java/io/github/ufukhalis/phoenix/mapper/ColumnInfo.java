package io.github.ufukhalis.phoenix.mapper;

import java.io.Serializable;

public class ColumnInfo implements Serializable {

    private String columnName;
    private Class<?> columnClass;
    private Object columnValue;

    public ColumnInfo(String columnName, Class<?> columnClass, Object columnValue) {
        this.columnName = columnName;
        this.columnClass = columnClass;
        this.columnValue = columnValue;
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

    public void setColumnValue(Object columnValue) {
        this.columnValue = columnValue;
    }

    public Object getColumnValue() {
        return columnValue;
    }
}
