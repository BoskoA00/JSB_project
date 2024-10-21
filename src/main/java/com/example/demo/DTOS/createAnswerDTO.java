package com.example.demo.DTOS;

public class createAnswerDTO {
    private String content;
    private Long questionId;

    public createAnswerDTO(Long questionId, String content){
        this.questionId=questionId;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getQuestionId() {
        return questionId;
    }


    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

}
