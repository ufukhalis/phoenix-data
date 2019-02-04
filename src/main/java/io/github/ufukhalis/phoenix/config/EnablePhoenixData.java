package io.github.ufukhalis.phoenix.config;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan(basePackages = "io.github.ufukhalis.phoenix.*")
public @interface EnablePhoenixData {
}
