package com.example.michael;

import java.io.Serializable;

public class News implements Serializable {
    public String title;
    public String author;
    public String publish_time;
    public String publish_house;
    public String ISBN;
    public int Icon;
    public String image_path;
    public News(){
        this.title = "TitleHere";
        this.author = "AuthorHere";
        this.publish_house = "Publish_houseHere";
        this.publish_time = "TimeHere";
        this.ISBN = "ISBNHere";
        this.Icon = R.drawable.desert;
        this.image_path = "NULL";
    }
}
