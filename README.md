[![Build Status](https://travis-ci.com/ufukhalis/phoenix-data.svg?branch=master)](https://travis-ci.com/ufukhalis/phoenix-data) [![Coverage Status](https://coveralls.io/repos/github/ufukhalis/phoenix-data/badge.svg)](https://coveralls.io/github/ufukhalis/phoenix-data)

Phoenix Data 
===================

This project goal is to make more easily configuration and using Apache Phoenix with Spring Boot based
applications. 

There are not good enough example of usage the Apache Phoenix, especially with Spring Boot. 
So ``Phoenix Data`` was born via this idea. For now, with this library you can easily connect
to Apache Phoenix and can execute the raw SQL queries. It has support for Async calls too.

This project uses the vavr library which is really good implement of functional Java.


Quick Start
---

First add dependency to your project.

    <dependency>
        <groupId>io.github.ufukhalis.phoenix</groupId>
        <artifactId>phoenix-data</artifactId>
        <version>0.0.4</version>
    </dependency>

Then create a class add `@EnablePhoenixData` annotation.

    @EnablePhoenixData
    @Configuration
    public class PhoenixConfig {
    
    }

And you need to add HOME_HADOOP your path or you can specify like below

    System.setProperty("hadoop.home.dir", "{hadoop.path}");

After then you need to add below properties to your application.properties file.

    phoenix.data.jdbcUrl = jdbc:phoenix:thin:url=http://localhost:8765;serialization=PROTOBUF
    phoenix.data.maxConnection = 10 # optional
    phoenix.data.minConnection = 5  # optional
    phoenix.data.basePackage = {your.root.package} # com.test
    phoenix.data.tableStrategy = CREATE # Other options are NONE and DROP_CREATE
    
After these configurations, let's create your repository class which is extends `PhoenixCrudRepository`.

    @Repository
    public class YourRepository extends PhoenixCrudRepository<TestEntity, Long> {
    
    }

Then we can define our basic `Entity` class.

    @Entity("tableName")
    public class TestEntity {
        
        @Column(value = "id", isPrimaryKey = true)
        private Long id;
        
        @Column("name")
        private String name;
        
    }    
    
Depends table creation strategy, when your application start to running
related strategy will run.


Then you can autowire your repository.

    @Autowired
    YourRepository yourRepository; 

Save your entity

    TestEntity entity = ...
    yourRepository.save(entity);

Find your entity by using its primary key

    Optional<TestEntity> entity = yourRepository.find(1L);

Delete your entity by using its primary key

    yourRepository.delete(1L);
    
Create query by using `PhoenixQuery` class.

    final PhoenixQuery phoenixQuery = new PhoenixQuery.Builder(YourEntity.class)
                    .select()
                    .build();

    YourEntity entity = yourRepository.find(phoenixQuery);

Also you can execute raw queries via `yourRepository`

    String rawSql = "drop table if exists javatest";
    int rowAffected = yourRepository.executeUpdate(rawSql);
    //...

Or async call

    String rawSql = "drop table if exists javatest";
    Future<Integer> rowAffected = yourRepository.executeUpdateAsync(rawSql);
    //...

Run your query and get results

    String rawSql = "select * from javatest";
    ResultSet rs = yourRepository.executeQuery(rawSql);
    while(rs.next()) {
        //..
    }

Or async call

    String rawSql = "select * from javatest";
    Future<ResultSet> resultSetFuture = yourRepository.executeQueryAsync(rawSql);
    resultSetFuture.mapTry(resultSet -> {
        //..
    });

Note
---

This project is still under development.

For more information about Apache Phoenix, check the site https://phoenix.apache.org/

For more information about vavr.io, check the site http://vavr-io.github.io

License
---

All code in this repository is licensed under the Apache License, Version 2.0. See [LICENCE](./LICENSE).