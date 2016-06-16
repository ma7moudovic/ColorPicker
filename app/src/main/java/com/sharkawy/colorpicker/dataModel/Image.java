package com.sharkawy.colorpicker.dataModel;

/**
 * Created by T on 6/15/2016.
 */
public class Image {
    String ImageURL ;

    public Image() {
    }

    public Image(String imageURL) {
        ImageURL = imageURL;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
