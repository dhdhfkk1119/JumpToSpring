package com.example.SUB.Service;

import com.example.SUB.Entity.Answer;
import com.example.SUB.Entity.Question;
import com.example.SUB.Entity.SiteUser;
import com.example.SUB.Repository.QuestionRepository;
import com.example.SUB.error.DataNotFoundException;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;



    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            Question question1 = question.get();
            question1.setView(question1.getView()+1);
            this.questionRepository.save(question1);
            return question1;
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
    
    // 질문에 대한 답변 정보 가져오기
    public void create(String subject, String content, SiteUser siteUser){
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setAuthor(siteUser);
        q.setCreatedate(LocalDateTime.now());
        this.questionRepository.save(q);

    }
        
    // page에 관한 설명 , page 정보 , 정렬 방식
    public Page<Question> getList(int page, String kw, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();

        if ("answerList".equalsIgnoreCase(sort)) {
            // JPQL을 사용하여 댓글 수 기준으로 내림차순 정렬
            Pageable pageable = PageRequest.of(page, 10);
            Specification<Question> spec = search(kw);
            return this.questionRepository.findAll((root, query, criteriaBuilder) -> {
                query.distinct(true);  // 중복을 제거
                Join<Question, Answer> answerJoin = root.join("answerList", JoinType.LEFT);
                query.orderBy(criteriaBuilder.desc(criteriaBuilder.size(root.get("answerList"))));
                return spec.toPredicate(root, query, criteriaBuilder);
            }, pageable);
        } else if ("view".equalsIgnoreCase(sort)) {
            sorts.add(Sort.Order.desc("view")); // 조회수 기준으로 내림차순 정렬
        } else if ("voter".equalsIgnoreCase(sort)) {
            // voter는 엔티티 자체의 속성이 아니라 다른 엔티티의 컬렉션 크기를 기준으로 정렬하므로, 서브쿼리를 사용하여 정렬합니다.
            // voterCount는 Question 엔티티에 정의된 가상의 컬럼이라고 가정합니다. 실제로는 이에 맞는 필드나 메서드를 구현해야 합니다.
            Pageable pageable = PageRequest.of(page, 10);
            Specification<Question> spec = search(kw);
            return this.questionRepository.findAll((root, query, criteriaBuilder) -> {
                query.distinct(true);  // 중복을 제거
                Join<Question, Answer> answerJoin = root.join("voter", JoinType.LEFT);
                query.orderBy(criteriaBuilder.desc(criteriaBuilder.size(root.get("voter"))));
                return spec.toPredicate(root, query, criteriaBuilder);
            }, pageable);
        } else {
            sorts.add(Sort.Order.desc("createdate")); // 기본적으로는 최신순으로 정렬
        }

        // 페이지 요청에 대한 설정 (페이지 번호를 0부터 시작하도록 수정)
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
    }


    // 수정에 관한 설명 수정을 저장한다. modify 메서드 추가
    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifydate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    //삭제에 관한 설명 delete 메서드 주가
    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    // 로그인한 사용자를 질문 엔티티에 추천인으로 저장하기 위해 vote 메서드를 추가
    public void vote(Question question, SiteUser siteUser) {
        if (question.getVoter().contains(siteUser)) {
            // 이미 투표한 경우에는 추가적인 투표를 허용하지 않음
            return;
        }
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    //검색 기능

    private Specification <Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root <Question> q, CriteriaQuery <?> query, CriteriaBuilder  cb) {
                query.distinct(true);  // 중복을 제거
                Join <Question, SiteUser> u1 = q.join("author", JoinType .LEFT);
                Join<Question, Answer > a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }


    // 카테고리 이름에 따른 조회수 순으로 정렬된 질문 목록 조회
    // 좋아요 순으로 정렬된 질문 목록 조회

}
