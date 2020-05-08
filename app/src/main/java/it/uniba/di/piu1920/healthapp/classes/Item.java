package it.uniba.di.piu1920.healthapp.classes;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;


public class Item implements java.io.Serializable{

    public  String name;
    public Drawable image;
    public ImageView im;
    String link;

    int id; String n2,n3,n4,n5,n6;

    public Item( int id, String name,String n2, String n3, String n4, String n5, String n6) {
        this.name = name;
        this.id = id;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;
        this.n5 = n5;
        this.n6 = n6;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getN2() {
        return n2;
    }

    public void setN2(String n2) {
        this.n2 = n2;
    }

    public String getN3() {
        return n3;
    }

    public void setN3(String n3) {
        this.n3 = n3;
    }



    public String getN4() {
        return n4;
    }
    public void setN4(String n4) {
        this.n4 = n4;
    }

    public String getN5() {
        return n5;
    }
    public void setN5(String n5) {
        this.n5 = n5;
    }

    public String getN6() {
        return n6;
    }
    public void setN6(String n6) {
        this.n6 = n6;
    }





    public Item(String name, String link) {
        this.name = name;
        this.link = link;
    }
    public Item(String name) {
        this.name = name;

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
