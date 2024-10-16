package com.cct.codebox.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;

import cn.hutool.core.util.StrUtil;
import com.cct.codebox.domain.RunCodeReq;
import com.cct.codebox.domain.RunCodeResp;
import com.cct.codebox.domain.TestCase;
import com.cct.codebox.domain.enums.JudgeResultEnum;
import com.cct.codebox.domain.enums.LanguageEnum;
import com.cct.codebox.domain.enums.RespCodeEnum;
import com.cct.codebox.exception.BusinessException;
import com.cct.codebox.service.RunCodeService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.cct.codebox.thread.ThreadPoolConfig.CODE_BOX_EXECUTOR;
import static com.cct.codebox.thread.ThreadPoolConfig.CODE_BOX_MONITOR_MEMORY_EXECUTOR;


/**
 * @BelongsProject: redmeatojbackend
 * @Author: cct
 * @Description: java运行代码服务
 */
@Service
@Slf4j
@Validated
public class JavaRunCodeServiceImpl implements RunCodeService {

    @Resource(name = CODE_BOX_EXECUTOR)
    private ExecutorService executor;

    @Resource(name = CODE_BOX_MONITOR_MEMORY_EXECUTOR)
    private ScheduledThreadPoolExecutor scheduledExecutor;

    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final OperatingSystem OS = SYSTEM_INFO.getOperatingSystem();


    private static final String STACK_OVERFLOW_ERROR = "Exception in thread \"main\" java.lang.OutOfMemoryError";
    private static final String OUT_OF_MEMORY_ERROR = "Exception in thread \"main\" java.lang.StackOverflowError";

    @Override
    public boolean support(String language) {
        return language.equals(LanguageEnum.JAVA.getLanguage());
    }

    @Override
    public RunCodeResp run(@Valid RunCodeReq runCodeReq) {

        RunCodeResp runCodeResp = new RunCodeResp();
        runCodeResp.setOutputContexts(new LinkedList<>());

        //1.保存临时文件
        String saveTmpFilePath = saveTmpFileAndGetPath(runCodeReq);

        //2.编译
        RunCodeResp compileFailResp = compileFile(runCodeResp, saveTmpFilePath);
        if (compileFailResp != null) {
            return compileFailResp;
        }

        //3.运行
        Integer memoryLimit = runCodeReq.getMemoryLimit();

        //构造运行命令
        memoryLimit /= 1024;
        if(memoryLimit <= 512){
            memoryLimit = 512;
        }
        String runCmd = String.format("java -Xmx%dm -Xss256k -Dfile.encoding=UTF-8 -cp %s Main", memoryLimit, FileUtil.getParent(saveTmpFilePath, 1));

        //程序输入内容
        List<TestCase> testCases = runCodeReq.getTestCases();

        int maxTimeConsume = 0;
        int maxMemoryConsume = 0;


        for (TestCase testCase : testCases) {
            runCodeResp.setLastTestCaseId(testCase.getTestCaseId());
            Process runProcess;
            long timeStart = System.currentTimeMillis();
            try {
                runProcess = Runtime.getRuntime().exec(runCmd);
            } catch (IOException e) {
                //返回前删除临时文件
                FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                log.error("运行失败 {}", e.getMessage());
                throw new BusinessException(RespCodeEnum.SYSTEM_ERROR, runCmd + "运行失败!");
            }

            InputStream errorStream = runProcess.getErrorStream();
            InputStream inputStream = runProcess.getInputStream();
            OutputStream outputStream = runProcess.getOutputStream();
            long pid = runProcess.pid();

            // 统计程序运行所消耗的最大内存，每10ms统计一次
            final AtomicLong maxMem = new AtomicLong(0);
            scheduledExecutor.schedule(() -> {
                OSProcess process = OS.getProcess((int) pid);
                if (process == null) {
                    return;
                }
                long memory = process.getResidentSetSize();
                maxMem.updateAndGet(oldValue -> Math.max(oldValue, memory));
            }, 10, TimeUnit.MILLISECONDS);

            // 输入数据，写入到进程的输入流
            executor.execute(() -> {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                try {
                    writer.append(testCase.getInputContent());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    //返回前删除临时文件
                    FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                    log.error("运行失败 {}", e.getMessage());
                    throw new BusinessException(RespCodeEnum.SYSTEM_ERROR, "代码运行输入失败");
                }
            });

            //收集进程正常输出内容
            Future<String> normalOutputFuture = executor.submit(() -> IoUtil.readUtf8(inputStream));


            //收集进程异常输出内容
            Future<String> errorOutputFuture = executor.submit(() -> IoUtil.readUtf8(errorStream));


            //设置进程最长时间，等待其运行结束
            boolean finished = false;
            try {
                finished = runProcess.waitFor(runCodeReq.getTimeLimit(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //返回前删除临时文件
                FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                log.error("runProcess.waitFor被中断 {}", e.getMessage());
                throw new BusinessException(RespCodeEnum.SYSTEM_ERROR, "runProcess.waitFor被中断");
            }
            //计算运行时间
            long timeConsume = System.currentTimeMillis() - timeStart;


            if (!finished) {
                // 如果进程运行超时，则终止进程，并返回
                runProcess.destroy();
                runCodeResp.setResult(JudgeResultEnum.RUNTIME_OUT.getDesc());
                runCodeResp.setCode(JudgeResultEnum.RUNTIME_ERROR.getCode());

                //返回前删除临时文件
                FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                return runCodeResp;
            }

            //获取进程运行结果
            String tmpNormalOutput ;
            String tmpErrorOutput ;
            try {
                tmpNormalOutput= normalOutputFuture.get();
                tmpErrorOutput = errorOutputFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            //如果有错误输出,即运行失败
            if (tmpErrorOutput.length() > 0) {
                runCodeResp.getOutputContexts().add(tmpErrorOutput);

                //判断是否是内存溢出错误
                if (StrUtil.startWith(tmpErrorOutput, STACK_OVERFLOW_ERROR) || StrUtil.startWith(tmpErrorOutput, OUT_OF_MEMORY_ERROR)) {
                    runCodeResp.setResult(JudgeResultEnum.MEMORY_OUT.getDesc());
                    runCodeResp.setCode(JudgeResultEnum.MEMORY_OUT.getCode());
                    //返回前删除临时文件
                    FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                    return runCodeResp;
                }

                runCodeResp.setResult(JudgeResultEnum.RUNTIME_ERROR.getDesc());
                runCodeResp.setCode(JudgeResultEnum.RUNTIME_ERROR.getCode());
                runCodeResp.setTimeConsume((int) timeConsume);
                //返回前删除临时文件
                FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                return runCodeResp;
            }

            //4.正常运行结束返回
            runCodeResp.getOutputContexts().add(tmpNormalOutput);
            int v = (int) (maxMem.get() / 1024);
            if (v > maxMemoryConsume) {
                runCodeResp.setMemoryConsume(v);
            }
            if ((int) timeConsume > maxTimeConsume) {
                runCodeResp.setTimeConsume((int) timeConsume);
            }

            //5.判断运行结果是否正确
            //代码正常运行结束，判断输出是否正确
            if (!Objects.equals(tmpNormalOutput, testCase.getOutputContent())) {
                runCodeResp.setResult(JudgeResultEnum.ANSWER_ERROR.getDesc());
                runCodeResp.setCode(JudgeResultEnum.ANSWER_ERROR.getCode());
                //返回前删除临时文件
                FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                return runCodeResp;
            }

        }

        runCodeResp.setResult(JudgeResultEnum.SUCCESS.getDesc());
        runCodeResp.setCode(JudgeResultEnum.SUCCESS.getCode());
        //返回前删除临时文件
        FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
        return runCodeResp;

    }


    private RunCodeResp compileFile(RunCodeResp runCodeResp, String saveTmpFilePath) {
        //2.编译

        //构造编译命令
        String compileCmd = String.format("javac -encoding UTF-8 %s", saveTmpFilePath);
        try {
            Process process = Runtime.getRuntime().exec(compileCmd);

            // 获取错误输出流（错误信息）
            InputStream errorStream = process.getErrorStream();

            //使用收集进程异常输出内容，防止阻塞
            //使用收集进程异常输出内容，防止阻塞
            Future<String> errorOutputFuture = executor.submit(() -> IoUtil.readUtf8(errorStream));

            // 等待进程执行完成，并设置超时时间
            boolean timeOut = process.waitFor(1, TimeUnit.MINUTES);
            String tmpErrorOutput = errorOutputFuture.get();
            // 如果有错误输出，即编译失败
            if (tmpErrorOutput.length() > 0) {
                runCodeResp.getOutputContexts().add(tmpErrorOutput);
                runCodeResp.setResult(JudgeResultEnum.COMPILE_ERROR.getDesc());
                runCodeResp.setCode(JudgeResultEnum.COMPILE_ERROR.getCode());
                //返回前删除临时文件
                FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                return runCodeResp;
            }

            if (!timeOut) {
                //返回前删除临时文件
                FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
                throw new BusinessException(RespCodeEnum.SYSTEM_ERROR, "代码编译超时");
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            //返回前删除临时文件
            FileUtil.del(FileUtil.getParent(saveTmpFilePath, 1));
            throw new BusinessException(RespCodeEnum.SYSTEM_ERROR, "代码编译失败");
        }
        return null;
    }

    @NotNull
    private String saveTmpFileAndGetPath(RunCodeReq runCodeReq) {
        //1.将代码保存
        //获取当前文件所在路径
        String projectPath = System.getProperty("user.dir");
        //拼接文件路径
        String saveTmpFilePath = Paths.get(projectPath, "tmp", "java",
                IdUtil.getSnowflakeNextIdStr(), "Main.java").toString();
        FileUtil.writeUtf8String(runCodeReq.getCode(), saveTmpFilePath);
        return saveTmpFilePath;
    }

}
