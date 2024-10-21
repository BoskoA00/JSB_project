package com.example.demo.DTOS;

public class UpdateAdRequestDTO {
    private String title;
    private String city;
    private String country;
    private Float price;
    private Float size;
    private Integer type;

    public UpdateAdRequestDTO(String title, String city, String country, Float price, Float size, Integer type) {
        this.title = title;
        this.city = city;
        this.country = country;
        this.price = price;
        this.size = size;
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getSize() {
        return this.size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
