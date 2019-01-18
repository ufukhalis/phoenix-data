Phoenix Data 
===================

This project goal is to make more easily configuration and using Apache Phoenix with Spring Boot based
applications. 

There are not good enough example of usage the Apache Phoenix, especially with Spring Boot. 
So ``Phoenix Data`` was born via this idea. For now, with this library you can easily connect
to Apache Phoenix and can execute the raw SQL queries. It has support for Async calls too.

This project uses the vavr library which is really good implement of functional Java.

Installation
---

For now, you can just clone the project to your local and using as a module or local maven artifacts.

Quick Start
---

After installation project to your local, you should add as a dependency.

    <dependency>
        <groupId>com.ufuk.phoenix</groupId>
        <artifactId>phoenix-data</artifactId>
        <version>0.0.1</version>
    </dependency>

Then you need to specify your `@SpringBootApplication` annotation.

    @SpringBootApplication(scanBasePackages = {"com.ufuk.phoenix.*", "{this.is.your.base.package}"})

And you need to add HOME_HADOOP your path or you can specify like below

    System.setProperty("hadoop.home.dir", "C:\\hbase");

After then you need to add below properties to your application.properties file.

    phoenix.data.jdbcUrl = jdbc:phoenix:thin:url=http://localhost:8765;serialization=PROTOBUF
    phoenix.data.maxConnection = 10 # optional
    phoenix.data.minConnection = 5  # optional
    
After these configurations, you are ready to use Phoenix Data repository class.

You can autowire the class like below

    @Autowired
    PhoenixRepository phoenixRepository;
    
Run your query via `phoenixRepository`

    String rawSql = "drop table if exists javatest";
    int rowAffected = phoenixRepository.executeUpdate(rawSql);
    //...

Or async call

    String rawSql = "drop table if exists javatest";
    Future<Integer> rowAffected = phoenixRepository.executeUpdateAsync(rawSql);
    //...

Run your query and get results

    String rawSql = "select * from javatest";
    ResultSet rs = phoenixRepository.executeQuery(rawSql);
    while(rs.next()) {
        //..
    }

Or async call

    String rawSql = "select * from javatest";
    Future<ResultSet> resultSetFuture = phoenixRepository.executeQueryAsync(rawSql);
    resultSetFuture.mapTry(resultSet -> {
        //..
    });

NOTE
---

This project is still under development.

For more information about Apache Phoenix, check the site https://phoenix.apache.org/

For more information about vavr.io, check the site http://vavr-io.github.io