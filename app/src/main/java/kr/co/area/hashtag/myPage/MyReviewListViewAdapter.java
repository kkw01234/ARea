package kr.co.area.hashtag.myPage;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import kr.co.area.hashtag.R;

public class MyReviewListViewAdapter extends BaseAdapter {
    private ArrayList<MyReviewListViewItem> items;
    Activity activity;

    public MyReviewListViewAdapter(Activity activity) {
        items = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return items.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.my_review_layout, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView imageView = convertView.findViewById(R.id.my_review_img);
        TextView restNameView = convertView.findViewById(R.id.rest_name_value);
        TextView contentView = convertView.findViewById(R.id.content_value);
        TextView dateView = convertView.findViewById(R.id.date_value);
        RatingBar ratingBar = convertView.findViewById(R.id.my_review_rating);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        MyReviewListViewItem myReviewListViewItem = items.get(position);

        // 아이템 내 각 위젯에 데이터 반영

        String url = "http://118.220.3.71:13565/download_file?category=download_review_image&u_id=" + myReviewListViewItem.getImg();
        Glide.with(activity).load(url).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(imageView);
        System.out.println("REST NAME : " + myReviewListViewItem.getRestName());
        restNameView.setText(myReviewListViewItem.getRestName());
        contentView.setText(myReviewListViewItem.getContent());
        ratingBar.setRating(myReviewListViewItem.getRate());
        dateView.setText(myReviewListViewItem.getDate());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) { return items.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String img, String restName, String content, String rate, String date) {
        MyReviewListViewItem item = new MyReviewListViewItem();

        item.setimg(img);
        item.setRestName(restName);
        item.setContent(content);
        item.setRate(Float.parseFloat(rate));
        item.setDate(date);

        items.add(item);
    }
}