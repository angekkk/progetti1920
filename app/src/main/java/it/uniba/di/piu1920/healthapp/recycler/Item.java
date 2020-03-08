package it.uniba.di.piu1920.healthapp.recycler;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;


public class Item implements java.io.Serializable{

    public  String name;
    public Drawable image;
    public ImageView im;
    String link;

    public Item(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Item(String name, Drawable image) {
        this.name = name;
        this.image = image;
    }

    public Item(String name, ImageView im) {
        this.name = name;
        this.im = im;
    }

    public ImageView getIm() {
        return im;
    }

    public void setIm(ImageView im) {
        this.im = im;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
