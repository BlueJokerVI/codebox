package com.cct.codebox;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cct.codebox.**.mapper")
public class CodeboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeboxApplication.class, args);
    }

}
