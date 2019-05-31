package kr.co.area.hashtag.myPage;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class MyReviewListViewItem {
    private String img;
    private String restName;
    private String content;
    private String date;
    private float rate;

    public void setimg(String img) { this.img = img; }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) { this.date = date; }

    public void setRate(float rate) { this.rate = rate; }

    public String getImg() {
        return img;
    }

    public String getRestName() {
        return restName;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {return date;}

    public float getRate() {return rate;}
}
