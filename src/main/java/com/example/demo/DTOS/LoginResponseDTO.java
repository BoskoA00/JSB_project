package com.example.demo.DTOS;

public class LoginResponseDTO {
    private UserDetailDTO user;
    private String token;

    public LoginResponseDTO(UserDetailDTO user, String token){
        this.user = user;
        this.token = token;
    }

    public UserDetailDTO getUser() {
        return user;
    }

    public void setUser(UserDetailDTO user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
