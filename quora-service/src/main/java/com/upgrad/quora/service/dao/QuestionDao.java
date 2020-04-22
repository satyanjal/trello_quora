package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

}
