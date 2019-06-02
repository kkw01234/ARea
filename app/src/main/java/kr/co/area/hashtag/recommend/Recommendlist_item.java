package kr.co.area.hashtag.recommend;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Recommendlist_item { private Drawable iconDrawable ;
    private String titleStr ;
    private String descStr ;
    public View.OnClickListener onClickListener;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
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
}