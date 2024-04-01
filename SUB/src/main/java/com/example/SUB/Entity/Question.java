package com.example.SUB.Entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;

    private LocalDateTime modifydate;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer view;

    // 조회수 증가 초기화 0 으로 시작
    public Integer getView() {
        return this.view != null ? this.view : 0; // 만약 view가 null이면 0을 반환하도록 수정
    }


}
