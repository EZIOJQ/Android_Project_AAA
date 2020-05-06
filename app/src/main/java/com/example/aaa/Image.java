package com.example.aaa;

class Image {
    private String image_name;
    private int image;
    Image(String image_name, int image){
        this.image = image;
        this.image_name = image_name;
    }

    String getImage_title(){
        return this.image_name;
    }
    int getImage(){
        return this.image;
    }
}
