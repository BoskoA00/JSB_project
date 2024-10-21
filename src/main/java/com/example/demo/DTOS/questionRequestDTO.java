package com.example.demo.DTOS;

import com.example.demo.Models.User;
import org.apache.juli.logging.Log;

public class questionRequestDTO {

    private String title;
    private String content;

    public questionRequestDTO(String title, String content){
        this.title = title;
        this.content = content;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getContent(){
        return this.content;
    }
    public void setContent(String content){
        this.title = title;
    }


}
