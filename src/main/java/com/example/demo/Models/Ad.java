    package com.example.demo.Models;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;

    import java.util.List;

    @Entity
    @Table(name = "ads")
    public class Ad {

        @Id
        @SequenceGenerator(
                name = "ads_sequence",
                sequenceName = "ads_sequence",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "ads_sequence"
        )
        private Long id;

        private String title;
        private String city;
        private String country;
        private Float price;
        private Float size;

        private Integer type;

        @ElementCollection
        @CollectionTable(name = "ad_images", joinColumns = @JoinColumn(name = "ad_id"))
        @Column(name = "image_path")
        private List<String> imagePaths;

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id",nullable = false)
        private User user;


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

        public User getUser(){
            return  this.user;
        }
        public void setUser(User user){
            this.user = user;
        }
    }