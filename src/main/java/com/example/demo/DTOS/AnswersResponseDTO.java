package com.example.demo.DTOS;

public class AnswersResponseDTO {
    private Long id;
    private String content;
    private UserDetailDTO user;
    private QuestionDetails question;

    public AnswersResponseDTO(Long id, String content, UserDetailDTO user, QuestionDetails question) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public QuestionDetails getQuestionId() {
        return question;
    }

    public void setQuestionId(QuestionDetails question) {
        this.question = question;
    }
}
