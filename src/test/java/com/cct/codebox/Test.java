package com.cct.codebox;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @BelongsProject: codebox
 * @Author: cct
 * @Description: TODO
 */
public class Test {
    @org.junit.jupiter.api.Test
    void contextLoads() throws IOException {

        String saveTmpFilePath = "E:\\012_redMeat_OJ\\codebox\\src\\test\\java\\com\\cct\\codebox\\Main.cpp";
        String prePath = new File(saveTmpFilePath).getParent();
        //构造编译命令
        String compileCmd = String.format("g++ -fno-asm -Wall -lm --static -O2 -std=c++11 -DONLINE_JUDGE -o %s\\Main %s", prePath, saveTmpFilePath);

        Process process = Runtime.getRuntime().exec(compileCmd);

    }
}
