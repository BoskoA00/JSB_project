package com.example.demo.DTOS;

public class UserLoginDTO {
    private String email;
    private String password;

    public UserLoginDTO(String email, String password){
        this.email = email;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
