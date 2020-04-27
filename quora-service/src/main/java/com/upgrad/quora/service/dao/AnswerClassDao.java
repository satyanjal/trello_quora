package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AnswerClassDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /*public List<QuestionEntity> getAllQuestions() {
        TypedQuery<QuestionEntity> query = entityManager.createQuery("SELECT q from QuestionEntity q", QuestionEntity.class);
        return query.getResultList();
    }

    public void updateQuestions(final QuestionEntity updatedQuestionEntity) {
        entityManager.merge(updatedQuestionEntity);
    }

    public QuestionEntity getQuestionByUuid(final String questionUuid) {
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).
                    setParameter("uuid", questionUuid).getSingleResult();
        } catch (NoResultException nre) {return null;}
    }

    public void deleteQuestion(QuestionEntity deleteQuestion) {
        entityManager.remove(deleteQuestion);
    }

    public List<QuestionEntity> getAllQuestionsByUser(final String userUuid) {
        TypedQuery<QuestionEntity> query = entityManager.
                createNamedQuery("questionsByUser", QuestionEntity.class).setParameter("user_id", userUuid);
        return query.getResultList();
    }*/

}
