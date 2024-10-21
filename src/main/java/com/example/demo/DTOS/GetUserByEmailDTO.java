package com.example.demo.DTOS;

import org.springframework.lang.NonNull;

public class GetUserByEmailDTO {

    private String email;

    public GetUserByEmailDTO(){}

    public GetUserByEmailDTO(String email){
        this.email = email;
    }
    public String getEmail(){
        return  this.email;
    }
    public void  setEmail(String email){
        this.email = email;
    }
}
