package io.github.ufukhalis.phoenix.query;

import io.github.ufukhalis.phoenix.mapper.AnnotationResolver;
import io.github.ufukhalis.phoenix.mapper.EntityInfo;
import io.github.ufukhalis.phoenix.mapper.QueryResolver;
import io.vavr.control.Option;

public class PhoenixQuery {

    private Class<?> entityClass;
    private String sql;

    private void Query() {

    }

    public static class Builder {

        private Class<?> entityClass;

        private String rawSql = "";

        public Builder(Class<?> entityClass) {
            this.entityClass = entityClass;
        }

        public Builder select(String ...fields) {
            final EntityInfo entityInfo = new AnnotationResolver().resolveClass(entityClass, Option.none());

            this.rawSql += QueryResolver.toSelectFrom(entityInfo, fields);

            return this;
        }

        public Builder where() {
            this.rawSql += " where ";

            return this;
        }

        public Builder condition(String key, Object value, Operator operator) {
            final String condition = key + operator.toValue() + QueryResolver.resolveTypeForSQLValue(value) + " ";

            this.rawSql += condition;

            return this;
        }

        public Builder or() {
            this.rawSql += " or ";

            return this;
        }

        public Builder and() {
            this.rawSql += " and ";

            return this;
        }

        public Builder limit(int value) {
            this.rawSql += " limit " + value;

            return this;
        }

        public PhoenixQuery build() {
            final PhoenixQuery query = new PhoenixQuery();
            query.entityClass = this.entityClass;
            query.sql = this.rawSql.replace("  ", " ");
            return query;
        }

    }

    public String sql() {
        return this.sql;
    }

    public Class<?> entityClass() {
        return this.entityClass;
    }

    public enum Operator {
        EQUAL("="),
        NOT_EQUAL("!="),
        GT(">"),
        GTE(">="),
        LT("<"),
        LTE("<=");

        private String str;

        Operator(String str) {
            this.str = str;
        }

        public String toValue() {
            return this.str;
        }
    }
}
