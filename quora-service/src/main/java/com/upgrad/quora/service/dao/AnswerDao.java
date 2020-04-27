package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerDao extends CrudRepository<AnswerEntity,Long> {


    @Query("select a.uuid, q.content, a.ans from AnswerEntity a, " +
            "QuestionEntity q where a.question.id = q.id and q.uuid = :questionUuId")
    List<Object[]> getAllByQuestion(@Param("questionUuId") String questionUuId);

}
