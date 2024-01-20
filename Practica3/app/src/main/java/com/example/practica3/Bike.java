package com.example.practica3;

import android.graphics.Bitmap;

public class Bike {

    private String image;
    private String owner;
    private String description;
    private String city;
    private Double longitude;
    private Double latitude;
    private String location;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }




    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Bike(String image, String owner, String description, String city, Double longitude, Double latitude, String location, Bitmap photo, String email) {
        this.image = image;
        this.owner = owner;
        this.description = description;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.photo = photo;
        this.email = email;
    }

    public Bike(){

    }

    public Bike(Bitmap photo, String owner, String description, String city,  String location, String email) {
        this.photo = photo;
        this.owner = owner;
        this.description = description;
        this.city = city;
        this.location = location;
        this.email=email;
    }

    private Bitmap photo;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
}
