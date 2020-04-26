package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    //answer/all/{questionId}
    @RequestMapping( method = RequestMethod.GET, path = "/answer/all/{questionId}")
    public List<AnswerEntity> allAnswers(@PathVariable("questionId") final String questionUuid){
        List<AnswerEntity> answers = answerService.allAnswers(questionUuid);
        return answers;
    }

}
