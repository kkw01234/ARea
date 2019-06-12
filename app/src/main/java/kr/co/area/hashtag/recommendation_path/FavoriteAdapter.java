package kr.co.area.hashtag.recommendation_path;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.area.hashtag.R;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private ArrayList<Recommendlist_item> holders =new ArrayList<>();
    private Activity activity;

    public FavoriteAdapter(Activity activity){
        this.activity= activity;
    }

    public FavoriteAdapter(ArrayList<Recommendlist_item> favoriteAdapters, Activity activity){
        this.holders = favoriteAdapters;
    }

   public class FavoriteViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        TextView textView2;
        TextView goodView;


        public FavoriteViewHolder(View v){
            super(v);
            this.imageView = v.findViewById(R.id.favorite_imageView);
            this.textView = v.findViewById(R.id.favorite_textView);
            this.textView2 = v.findViewById(R.id.favorite_textView2);
            this.goodView = v.findViewById(R.id.favorite_rate);
        }

       void onBind(Recommendlist_item r) {
           textView.setText(r.getTitle());
           textView2.setText(r.getDesc());
           Glide.with(activity).load(r.getUrl()).into(imageView);
           //goodView.setText(r.getGood());
       }

   }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        Log.i("onCreateViewHolder", position+" ");
       View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_list_item, viewGroup, false);
       return new FavoriteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder viewHolder, int position) {
        Log.i("onBindViewHolder", position+" ");
        viewHolder.onBind(holders.get(position));
    }



    @Override
    public int getItemCount() {
        return holders.size();
    }

    public void additem(Recommendlist_item item){
        holders.add(item);
    }
}
