package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnswerDao extends CrudRepository <AnswerEntity,Long> {

    public List<AnswerEntity> findAllByQuestion(String question_id);
}
