package com.cct.codebox.consumer;

import com.cct.codebox.dao.question.QuestionDao;
import com.cct.codebox.dao.submitrecord.SubmitRecordDao;
import com.cct.codebox.domain.RunCodeReq;
import com.cct.codebox.domain.RunCodeResp;
import com.cct.codebox.domain.enums.JudgeResultEnum;
import com.cct.codebox.domain.submitRecord.SubmitRecord;
import com.cct.codebox.service.RunCodeServiceManagerChain;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.cct.codebox.constant.MQConstant.QUESTION_SUBMIT_CONSUME_GROUP;
import static com.cct.codebox.constant.MQConstant.QUESTION_SUBMIT_TOPIC;

/**
 * @author cct
 * @description 监听mq收到的运行代码请求
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = QUESTION_SUBMIT_CONSUME_GROUP, topic = QUESTION_SUBMIT_TOPIC)
public class QuestionSubmitListener implements RocketMQListener<RunCodeReq> {

    @Resource
    private RunCodeServiceManagerChain runCodeServiceManagerChain;

    @Resource
    private SubmitRecordDao submitRecordDao;

    @Resource
    private QuestionDao questionDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(RunCodeReq runCodeReq) {

        //1.运行用户提交代码
        RunCodeResp runCodeResp = runCodeServiceManagerChain.runCode(runCodeReq);

        //2.将运行结果更新到数据库
        Long submitRecordId = runCodeReq.getSubmitRecordId();
        SubmitRecord existed = submitRecordDao.getById(submitRecordId);

        if (existed == null) {
            log.error("提交记录不存在");
            return;
        }

        existed.setJudgeResult(runCodeResp.getCode());
        existed.setLastRunCase(runCodeResp.getLastTestCaseId());
        existed.setLanguage(runCodeReq.getLanguage());
        existed.setSubmitContext(runCodeReq.getCode());
        existed.setTimeConsume(runCodeResp.getTimeConsume());
        existed.setMemoryConsume(runCodeResp.getMemoryConsume());
        if (!submitRecordDao.updateById(existed)) {
            log.error("更新提交记录失败，提交记录id{}", existed.getId());
            throw new RuntimeException("更新提交记录失败");
        }

        Long questionId = existed.getQuestionId();
        if (existed.getJudgeResult() == JudgeResultEnum.SUCCESS.getCode()) {
            //增加题目ac 与 提交次数
            if (!questionDao.addQuestionSubmitNumAndAcNum(questionId)) {
                log.error("更新题目提交次数与ac次数失败，题目id{}，提交记录id{}", questionId, submitRecordId);
                throw new RuntimeException("更新题目提交次数与ac次数失败");
            }
        } else {
            //增加题目提交次数
            if (!questionDao.addQuestionSubmitNum(questionId)) {
                log.error("更新题目提交次数失败，题目id{}，提交记录id{}", questionId, submitRecordId);
                throw new RuntimeException("更新题目提交次数失败");
            }
        }

    }
}
