package com.example.demo.DTOS;

import com.example.demo.Models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.List;

public class AdResponseDTO {

    private Long id;
    private String title;
    private String city;
    private String country;
    private Float price;
    private Float size;
    private Integer type;
    private List<String> imagePaths;
    private UserDetailDTO user;

    public AdResponseDTO(Long id, String title, String city, String country, Float price, Float size, Integer type, List<String> imagePaths, UserDetailDTO user){
        this.city = city;
        this.id = id;
        this.country = country;
        this.title = title;
        this.price = price;
        this.size = size;
        this.type = type;
        this.imagePaths = imagePaths;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public UserDetailDTO getUser(){
        return  this.user;
    }
    public void setUser(UserDetailDTO user){
        this.user = user;
    }
}
