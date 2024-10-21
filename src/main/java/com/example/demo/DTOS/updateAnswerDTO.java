package com.example.demo.DTOS;

public class updateAnswerDTO {
    private String content;

    public updateAnswerDTO(){}
    public updateAnswerDTO(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
