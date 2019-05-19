package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class PlaceTask extends AsyncTask<String, Void, String> {
    Activity activity;


    public PlaceTask(Activity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection urlConnection = new RequestHttpURLConnection();
        String URL="";
        ArrayList<Parameter> params = new ArrayList<>();

        String result = urlConnection.request(URL,params,activity.getApplicationContext());

        return result;
    }
}
