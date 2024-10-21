package com.example.demo.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Answers")
public class Answer {
    @Id
    @SequenceGenerator(name = "answer_sequence", sequenceName = "answer_sequence", allocationSize = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "answer_sequence")
    private  Long id;
    private String content;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn( name = "user_id", nullable = false)
    private User user;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "question_id", nullable = false)
    private Question question;

    public String getContent(){
        return  this.content;
    }
    public void setContent(String content){
        this.content = content;
    }
    public User getUser(){
        return this.user;
    }
    public void setUser(User user){
        this.user = user;
    }
    public Question getQuestion(){
        return this.question;
    }
    public void setQuestion( Question question){
        this.question = question;
    }

    public Long getId() {
        return id;
    }

}
