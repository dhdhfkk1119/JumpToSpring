package com.example.SUB.Repository;

import com.example.SUB.Entity.Answer;
import com.example.SUB.Entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer,Integer> {

    Page<Answer> findAll(Specification<Answer> spec, Pageable pageable);
    @Query("SELECT a FROM Answer a LEFT JOIN a.voter v WHERE a.question = :question GROUP BY a.id ORDER BY COUNT(v) DESC")
    Page<Answer> findAllByQuestionOrderByVoterCountDesc(@Param("question") Question question, Pageable pageable);


}
