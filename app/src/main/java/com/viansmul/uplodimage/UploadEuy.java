package com.viansmul.uplodimage;

public class UploadEuy {
    private  String mName;
    private String mImageUrl;

    public UploadEuy(){
        //empty constructor needed
    }
    public UploadEuy(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
    }
    public String getmName() {
        return mName;
    }
    public void setmName(String name){
        mName = name;
    }
    public String getImageUrl(){
        return mImageUrl;
    }
    public void setmImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
