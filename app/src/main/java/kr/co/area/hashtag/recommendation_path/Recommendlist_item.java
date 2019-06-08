package kr.co.area.hashtag.recommendation_path;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.bumptech.glide.Glide;

public class Recommendlist_item {
    private Drawable iconDrawable ;
    private String imageurl;
    private String titleStr ;
    private String descStr ;
    private int good;
    public View.OnClickListener onClickListener;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setUrl(String url){
            this.imageurl = url;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setGood(int good) {
        this.good = good;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }



    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public String getUrl(){
        return this.imageurl;
    }
    public int getGood() {
        return good;
    }

}