package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;



    // 1st API /question/{questionId}/answer/create

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionUuid, final AnswerRequest answerRequest,
                                                       @RequestHeader("authorization") final String authorization) throws InvalidQuestionException, AuthorizationFailedException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(new Date());
        answerEntity.setUuid(UUID.randomUUID().toString());
        final AnswerEntity createdAnswer = answerService.createAnswer(answerEntity, authorization, questionUuid);
        final AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getId().toString()).status("QUESTION CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    //2nd API

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@PathVariable("answerId") final String answerUuid, final AnswerEditRequest answerEditRequest,
                                                         @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        String content = answerEditRequest.getContent();
        answerService.editAnswer(content, answerUuid, authorization);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerUuid).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }


    //4th API

    @RequestMapping( method = RequestMethod.GET, path = "/answer/all/{questionId}")
    public ResponseEntity<List<AnswerDetailsResponse>> allAnswers(@PathVariable("questionId") final String questionUuid,
                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        /*
            If the access token provided by the user does not exist in the database
            throw "AuthorizationFailedException" with the
            message code - 'ATHR-001' and message - 'User has not signed in'.

            If the user has signed out, throw "AuthorizationFailedException"
            with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to get the answers'.

            If the question with uuid whose answers are to be retrieved from the database does not exist in the database,
            throw "InvalidQuestionException"
            with the message code - 'QUES-001'
            and message - 'The question with entered uuid whose details are to be seen does not exist'.

            Else, return "uuid" of the answer,
            "content" of the question
            and "content" of all the answers posted for that particular question
            from the database in the JSON response with the corresponding HTTP status.

         */

        List<Object[]> answers = answerService.allAnswers(questionUuid,authorization);

        List<AnswerDetailsResponse>  answerDetailsResponses = new ArrayList<AnswerDetailsResponse>();

        Iterator answersIterator = answers.iterator();

       while(answersIterator.hasNext()){

           Object[] currentAnswer = (Object[]) answersIterator.next();

            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId((String) currentAnswer[0]);
            answerDetailsResponse.setQuestionContent((String) currentAnswer[1]);
            answerDetailsResponse.setAnswerContent((String) currentAnswer[2]);

            answerDetailsResponses.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);

    }

}
