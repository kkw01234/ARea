package kr.co.area.hashtag.myPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.main.RestActivity;

public class MyFavoriteListViewAdapter extends BaseAdapter {
    private ArrayList<MyFavoriteListViewItem> items;
    Activity activity;

    public MyFavoriteListViewAdapter(Activity activity) {
        items = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.my_favorite_layout, parent, false);
        }
        convertView.setTag(pos);
        convertView.setOnClickListener((v) -> {
            int index = (Integer) v.getTag();
            MyFavoriteListViewItem item = items.get(index);
            activity.startActivity(new Intent(activity, RestActivity.class).putExtra("id", item.getRestId()));
        });

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView imageView = convertView.findViewById(R.id.my_review_img);
        TextView restNameView = convertView.findViewById(R.id.rest_name_value);
        TextView addressView = convertView.findViewById(R.id.address_value);
        TextView scoreView = convertView.findViewById(R.id.score_value);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        MyFavoriteListViewItem myFavoriteListViewItem = items.get(position);

        // 아이템 내 각 위젯에 데이터 반영

        String url = "http://118.220.3.71:13565/download_file?category=download_review_image&u_id=" + myFavoriteListViewItem.getImg() + "&google_id=x";
        Glide.with(activity).load(url).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(imageView);
        restNameView.setText(myFavoriteListViewItem.getRestName());
        addressView.setText(myFavoriteListViewItem.getAddress());
        scoreView.setText(myFavoriteListViewItem.getScore() + "점");

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String img, String restId, String restName, String address, String score) {
        MyFavoriteListViewItem item = new MyFavoriteListViewItem();

        item.setimg(img);
        item.setRestId(restId);
        if(restName.length() > 12) restName = restName.substring(0, 12) + "..";
        item.setRestName(restName);
        item.setAddress(address);
        double sco = Double.parseDouble(score);
        sco = Math.round(sco * 100) / 100.0;
        item.setScore(Double.toString(sco));

        items.add(item);
    }
}