package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class AnswerController {

    @Autowired
    private AnswerService answerService;


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
