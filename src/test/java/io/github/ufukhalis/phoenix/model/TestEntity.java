package io.github.ufukhalis.phoenix.model;

import io.github.ufukhalis.phoenix.mapper.Column;
import io.github.ufukhalis.phoenix.mapper.Entity;

@Entity("tableName")
public class TestEntity {

    @Column(value = "id", isPrimaryKey = true)
    private Long id;

    @Column("name")
    private String name;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
