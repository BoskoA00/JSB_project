package com.example.demo.DTOS;

import java.util.List;

public class QuestionResponseDTO {
    private Long id;
    private String title;
    private String content;
    private UserDetailDTO user;
    private List<QuestionAnswers> answers;

    public QuestionResponseDTO(Long id, String title, String content, UserDetailDTO user,List<QuestionAnswers> answers){
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public UserDetailDTO getUser(){
        return this.user;
    }

    public void setUser(UserDetailDTO user) {
        this.user = user;
    }

    public List<QuestionAnswers> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswers> answers) {
        this.answers = answers;
    }
}
