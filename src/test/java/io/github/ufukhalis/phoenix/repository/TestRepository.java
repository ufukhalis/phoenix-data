package io.github.ufukhalis.phoenix.repository;

import io.github.ufukhalis.phoenix.data.PhoenixCrudRepository;
import io.github.ufukhalis.phoenix.model.TestEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TestRepository extends PhoenixCrudRepository<TestEntity, Long> {
}
