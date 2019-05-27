package kr.co.area.hashtag.main;

import android.widget.RatingBar;

public class reviewListViewItem {
    private float reviewpoint ;
    private String reviewname ;
    private String reviewtext ;

    public void setStar(float point) {
        reviewpoint = point ;
    }
    public void setName(String name) { reviewname = name ;  }
    public void setText(String text) {
        reviewtext = text ;
    }

    public float getStar() {
        return this.reviewpoint ;
    }
    public String getName() {
        return this.reviewname ;
    }
    public String getText() {
        return this.reviewtext ;
    }
}
