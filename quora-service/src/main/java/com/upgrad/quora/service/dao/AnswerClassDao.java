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


    public void updateAnswers(final AnswerEntity updatedAnswerEntity) {
        entityManager.merge(updatedAnswerEntity);
    }

    public AnswerEntity getAnswerByUuid(final String answerUuid) {
        try {
            return entityManager.createNamedQuery( "answerByUuid", AnswerEntity.class).
                    setParameter("uuid", answerUuid).getSingleResult();
        } catch (NoResultException nre) {return null;}
    }


    public void deleteAnswer(AnswerEntity deleteAnswer) {
        entityManager.remove(deleteAnswer);
    }



}
