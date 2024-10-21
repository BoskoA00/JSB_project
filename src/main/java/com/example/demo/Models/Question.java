package com.example.demo.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.LongStream;

@Entity
@Table(name = "Questions")
public class Question {
    @Id
    @SequenceGenerator(name = "question_sequence", sequenceName = "questions_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "questions_sequence")
    private Long id;
    private String title;
    private String content;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private  User user;

    @JsonIgnore
    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    public Long getId(){
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle(){
        return this.title;
    }
    public String getContent(){
        return this.content;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setContent(String content){
        this.content=content;
    }
    public void setUser(User user){
        this.user = user;
    }
    public User getUser(){
        return this.user;
    }
    public List<Answer> getAnswers(){
        return  this.answers;
    }
    public void setAnswers(List<Answer> answers){
        this.answers = answers;
    }


}
