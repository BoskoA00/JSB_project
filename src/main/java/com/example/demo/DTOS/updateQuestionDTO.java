package com.example.demo.DTOS;

public class updateQuestionDTO {
    private String title;
    private String content;

    public updateQuestionDTO(String title, String content){
        this.title =title;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
