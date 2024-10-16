package com.cct.codebox.dao.question;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cct.codebox.dao.question.mapper.QuestionMapper;
import com.cct.codebox.domain.question.Question;
import org.springframework.stereotype.Repository;

/**
* @author cct
* @description 针对表【question】的数据库操作Dao实现
* @createDate 2024-10-14 20:20:24
*/
@Repository
public class QuestionDao extends ServiceImpl<QuestionMapper, Question>{

    public boolean addQuestionSubmitNumAndAcNum(Long questionId) {
        return lambdaUpdate().setSql("question_submit_num = question_submit_num + 1 , question_ac_num = question_ac_num + 1").eq(Question::getId, questionId).update();
    }

    public boolean addQuestionSubmitNum(Long questionId) {
        return lambdaUpdate().setSql("question_submit_num = question_submit_num + 1").eq(Question::getId, questionId).update();
    }

}




