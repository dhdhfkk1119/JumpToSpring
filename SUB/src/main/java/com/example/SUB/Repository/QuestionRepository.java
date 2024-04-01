package com.example.SUB.Repository;

import com.example.SUB.Entity.Answer;
import com.example.SUB.Entity.Question;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Integer> {

    Page<Question> findAll(Pageable pageable);

    //검색 기능 findall 메서드는 specification 과
    // pageable객체를 사용하여 db에서 questuin엔티티를 조회한 결과를 페이징하여 반환
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
    @Query("SELECT COUNT(a) FROM Question q JOIN q.answerList a WHERE q.id = :questionId")
    int countAnswersByQuestionId(Integer questionId);
}

