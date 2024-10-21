package com.example.demo.DTOS;

public class QuestionAnswers {
    private Long id;
    private String content;
    private UserDetailDTO user;

    public QuestionAnswers(Long id, String content, UserDetailDTO user){
        this.content = content;
        this.id = id;
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUser(UserDetailDTO user) {
        this.user = user;
    }

    public UserDetailDTO getUser() {
        return user;
    }
}
