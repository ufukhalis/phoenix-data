package io.github.ufukhalis.phoenix.mapper;

import io.vavr.collection.List;

import java.io.Serializable;

public class EntityInfo implements Serializable {

    private String tableName;
    private List<ColumnInfo> columnInfo;

    public EntityInfo(String tableName, List<ColumnInfo> columnInfo) {
        this.tableName = tableName;
        this.columnInfo = columnInfo;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setColumnInfo(List<ColumnInfo> columnInfo) {
        this.columnInfo = columnInfo;
    }

    public List<ColumnInfo> getColumnInfo() {
        return columnInfo;
    }
}
