package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenzufeng
 * @SpringBootApplication 开启Spring配置，扫描所有注解
 * @MapperScan(basePackages = "com.example.dao")：不用在每一个dao文件中添加@Mapper
 */
@SpringBootApplication
public class SpringBootMyBatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMyBatisApplication.class, args);
    }

}
