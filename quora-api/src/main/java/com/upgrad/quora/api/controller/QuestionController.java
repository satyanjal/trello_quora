package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(new Date());
        questionEntity.setUuid(UUID.randomUUID().toString());
        final QuestionEntity createdQuestion = questionService.createQuestion(questionEntity, authorization);
        final QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getId().toString()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestion(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final List<QuestionEntity> allQuestions = questionService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntity questionEntity: allQuestions){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(questionEntity.getUuid());
            questionDetailsResponse.setContent(questionEntity.getContent());
            questionDetailsResponses.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") final String questionUuid, final QuestionEditRequest questionEditRequest,
                                                             @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        String content = questionEditRequest.getContent();
        questionService.editQuestion(content, questionUuid, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionUuid).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionUuid,
                                                             @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        questionService.deleteQuestion(questionUuid, authorization);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionUuid).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userUuid,
            @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        final List<QuestionEntity> allQuestions = questionService.getAllQuestionsByUser(userUuid, authorization);
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntity questionEntity: allQuestions){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(questionEntity.getUuid());
            questionDetailsResponse.setContent(questionEntity.getContent());
            questionDetailsResponses.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }
}
