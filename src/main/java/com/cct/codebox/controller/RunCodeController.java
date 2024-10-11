package com.cct.codebox.controller;

import com.cct.codebox.domain.BaseResponse;
import com.cct.codebox.domain.RunCodeReq;
import com.cct.codebox.domain.RunCodeResp;
import com.cct.codebox.service.RunCodeServiceManagerChain;
import com.cct.codebox.utils.RespUtils;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @BelongsProject: codebox
 * @Author: cct
 * @Description: 运行代码控制器
 */
@RestController
@RequestMapping("/codeBox")
@Api(tags = "运行代码接口")
public class RunCodeController {

    @Resource
    private RunCodeServiceManagerChain runCodeServiceManagerChain;

    @PostMapping("/runCode")
    BaseResponse<RunCodeResp> runCode(@Valid @RequestBody RunCodeReq runCodeReq) {
        RunCodeResp runCodeResp = runCodeServiceManagerChain.runCode(runCodeReq);
        return RespUtils.success(runCodeResp);
    }
}
