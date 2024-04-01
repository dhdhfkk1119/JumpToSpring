package com.example.SUB.Service;

import com.example.SUB.Entity.Answer;
import com.example.SUB.Entity.Question;
import com.example.SUB.Entity.SiteUser;
import com.example.SUB.Repository.AnswerRepository;
import com.example.SUB.error.DataNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreatedate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    // 댓글에 관한 수정 메서드 추가
    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    // 댓글에 관한 수정 메서드 추가
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifydate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    //댓글에 관한 삭제 메서드 추가
    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    //질문 추천 기능 메서드 작성
    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }


    // 댓글 페이지 가져오기
    public Page<Answer> getAnswerList(Question question, int page, String sort, String kw) {
        Pageable pageable;
        Specification<Answer> spec;

        if ("voter".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, 10);
            spec = (root, query, criteriaBuilder) -> {
                Join<Answer, SiteUser> voterJoin = root.join("voter", JoinType.LEFT);
                query.groupBy(root.get("id"));
                query.orderBy(criteriaBuilder.desc(criteriaBuilder.count(voterJoin)));
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            };
        } else {
            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(Sort.Order.desc("createdate"));
            pageable = PageRequest.of(page, 10, Sort.by(sorts));

            // 여기서 검색 조건을 만듭니다.
            spec = (root, query, criteriaBuilder) -> {
                // 예시: 답변 내용(content)에 대한 Like 검색
                if (kw != null && !kw.isEmpty()) {
                    String pattern = "%" + kw + "%";
                    return criteriaBuilder.like(root.get("content"), pattern);
                }
                // 만약 다른 검색 조건이 필요하다면 여기에 추가할 수 있습니다.
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            };
        }

        return this.answerRepository.findAll(spec, pageable);
    }


}
