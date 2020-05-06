package com.example.aaa;

class Image {
    private String image_name;
    private String image;
    private int uid;

    Image(String image_name, String image, int uid){
        this.image = image;
        this.uid = uid;
        this.image_name = image_name;
    }

    void setUid(int uid){this.uid = uid;}
    void setImage_name(String image_name){this.image_name = image_name;}
    void setImage(String image){this.image = image;}
    String getImage_title(){
        return this.image_name;
    }
    int getUid(){return this.uid;}
    String getImage(){
        return this.image;
    }
}
