package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class GetAllFavoriteTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public GetAllFavoriteTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/get_all_favorite";
        ArrayList<Parameter> params = new ArrayList<>();
        String result = conn.request(url, params, activity.getApplicationContext());
        return result;
    }
}