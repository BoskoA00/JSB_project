package com.example.demo.DTOS;

public class QuestionDetails {
    private Long id;
    private String title;
    private String content;
    private UserDetailDTO user;

    public QuestionDetails(Long id, String title, String content, UserDetailDTO user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
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

    public UserDetailDTO getUser() {
        return user;
    }

    public void setUser(UserDetailDTO user) {
        this.user = user;
    }

}
