package com.example.SUB.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    //Set 자료형은 중복이 안됨 그렇기에 개인당 추천한번
    @ManyToMany
    Set<SiteUser> voter;

    private LocalDateTime modifydate;

}
