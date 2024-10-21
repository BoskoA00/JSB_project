package com.example.demo.DTOS;

public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Integer role;

    public UpdateUserDTO(){}

    public UpdateUserDTO(String firstName, String lastName, String email, String password, Integer role)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public Integer getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
