package kr.co.area.hashtag.recommendation_path;

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

public class MyRouteListViewAdapter extends BaseAdapter {
    private ArrayList<MyRouteListViewItem> items;
    Activity activity;

    public MyRouteListViewAdapter(Activity activity) {
        items = new ArrayList<>();
        this.activity = activity;
    }

    public void clearList() {
        items.clear();
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
            convertView = inflater.inflate(R.layout.route_list_item, parent, false);
        }
        convertView.setTag(pos);
        convertView.setOnClickListener((v) -> {
            int index = (Integer) v.getTag();
            MyRouteListViewItem item = items.get(index);
            activity.startActivity(new Intent(activity, RestActivity.class).putExtra("id", item.getRestId()));
        });

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView restNameView = convertView.findViewById(R.id.route_restname);
        TextView seqView = convertView.findViewById(R.id.route_sequence);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        MyRouteListViewItem myRouteListViewItem = items.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        restNameView.setText(myRouteListViewItem.getRestName());
        seqView.setText(Integer.toString(myRouteListViewItem.getSequence()) + ".");

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
    public void addItem(String restId, String restName, int seq) {
        MyRouteListViewItem item = new MyRouteListViewItem();

        item.setRestId(restId);
        item.setRestName(restName);
        item.setSequence(seq);

        items.add(item);
    }
}