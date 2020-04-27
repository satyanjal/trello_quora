package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerClassDao;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    AnswerDao answerDao;

    @Autowired
    AnswerClassDao answerClassDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserAuthDao userAuthDao;


    //API 1
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String authorizationToken, final String questionUuid)
            throws InvalidQuestionException, AuthorizationFailedException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        answerEntity.setQuestion(questionEntity);

        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        answerEntity.setUser(userAuthEntity.getUser());
        return answerClassDao.createAnswer(answerEntity);
    }

    //API 2
    @Transactional(propagation = Propagation.REQUIRED)
    public void editAnswer(final String content, String answerUuid, final String authorizationToken)
            throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authorizationToken);
        AnswerEntity answerEntity = answerClassDao.getAnswerByUuid(answerUuid);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        } else if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else if (!userAuthEntity.getUser().getId().equals(answerEntity.getUser().getId())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        answerEntity.setAns(content);
        answerClassDao.updateAnswers(answerEntity);
    }

    //API 3
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(String answerUuid, final String authorizationToken)
            throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authorizationToken);
        AnswerEntity answerEntity = answerClassDao.getAnswerByUuid(answerUuid);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the answer");
        } else if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else if (!userAuthEntity.getUser().getId().equals(answerEntity.getUser().getId()) && userAuthEntity.getUser().getRole() != "admin") {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        answerClassDao.deleteAnswer(answerEntity);
    }


    //API 4
    public List<Object[]> allAnswers(String questionUuId,final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        List<Object[]> answers = answerDao.getAllByQuestion(questionUuId);
        return answers;
    }
}
