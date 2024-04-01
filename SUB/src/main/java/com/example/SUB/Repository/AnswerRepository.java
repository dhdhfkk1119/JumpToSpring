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

    Page<Answer> findAllByQuestion(Question question, Pageable pageable);

    @Query("SELECT e "
            + "FROM Answer e "
            + "WHERE e.question = :question "
            + "ORDER BY SIZE(e.voter) DESC, e.createdate")
    Page<Answer> findAllByQuestionOrderByVoter(@Param("question") Question question, Pageable pageable);


}
