package com.example.demo.Repositories;

import com.example.demo.Models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {

    @Query("SELECT q FROM Question q WHERE q.user.id = ?1")
    List<Question> getQuestionsByUserId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Question q WHERE q.user.id = ?1")
    void deleteByUser(Long userId);
}
