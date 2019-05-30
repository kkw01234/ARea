package kr.co.area.hashtag.main;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.RatingBar;

public class reviewListViewItem {
    private float reviewpoint ;
    private String reviewname ;
    private String reviewtext ;
    private Bitmap iconDrawable ;

    public void setIcon(Bitmap icon) { iconDrawable = icon ; }
    public void setStar(float point) {
        reviewpoint = point ;
    }
    public void setName(String name) { reviewname = name ;  }
    public void setText(String text) {
        reviewtext = text ;
    }

    public Bitmap getIcon() {return  this.iconDrawable ; }
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
