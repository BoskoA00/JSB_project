package com.example.demo.Repositories;

import com.example.demo.Models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer,Long> {

    @Query("SELECT a FROM Answer a WHERE a.user.id =?1")
    List<Answer> getAnswersByUserId(Long userId);

    @Query("SELECT a FROM Answer a WHERE a.question.id =?1")
    List<Answer> getAnswersByQuestionId(Long questionId);

    @Modifying
    @Transactional
    @Query("DELETE  FROM Answer a WHERE a.user.id = ?1")
    void deleteByUser(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE  FROM Answer a WHERE a.question.id = ?1")
    void deleteByQuestion(Long questionId);

}
