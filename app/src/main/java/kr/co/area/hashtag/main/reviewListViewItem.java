package kr.co.area.hashtag.main;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.RatingBar;

public class reviewListViewItem {
    private float reviewpoint ;
    private String reviewname ;
    private String reviewtext ;
    private String reviewImg ;
    private String reviewdate;

    public void setIcon(String img) { reviewImg = img ; }
    public void setStar(float point) {
        reviewpoint = point ;
    }
    public void setName(String name) { reviewname = name ;  }
    public void setText(String text) {
        reviewtext = text ;
    }
    public void setDate(String date) {
        reviewdate = date ;
    }

    public String getIcon() {return  this.reviewImg ; }
    public float getStar() {
        return this.reviewpoint ;
    }
    public String getName() {
        return this.reviewname ;
    }
    public String getText() {
        return this.reviewtext ;
    }
    public String getDate() {
        return this.reviewdate ;
    }
}
