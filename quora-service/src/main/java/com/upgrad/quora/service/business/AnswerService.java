package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private  AnswerDao answerDao;

    public List<AnswerEntity> allAnswers(String questionId) {

        List<AnswerEntity> answers = answerDao.findAllByQuestion(questionId);
        return answers;
    }
}
