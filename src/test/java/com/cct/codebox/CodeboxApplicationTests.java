package com.cct.codebox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class CodeboxApplicationTests {

    @Test
    void contextLoads() throws IOException {

        String saveTmpFilePath = "E:\\012_redMeat_OJ\\codebox\\src\\test\\java\\com\\cct\\codebox\\Main.cpp";
        //构造编译命令
        String compileCmd = String.format("g++ -fno-asm -Wall -lm --static -O2 -std=c++11 -DONLINE_JUDGE -o Main %s", saveTmpFilePath);
        Process process = Runtime.getRuntime().exec(compileCmd);


    }

}
