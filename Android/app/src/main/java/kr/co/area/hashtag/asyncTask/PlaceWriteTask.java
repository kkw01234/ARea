package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class PlaceWriteTask extends AsyncTask<String, Void, String> {
    Activity activity;


    public PlaceWriteTask(Activity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection urlConnection = new RequestHttpURLConnection();
        String URL="http://118.220.3.71:13565/insert_place";
        ArrayList<Parameter> params = new ArrayList<>();
        System.out.println(strings[0]);
        params.add(new Parameter("rest_google_id",strings[0]));
        params.add(new Parameter("restName",strings[1]));
        params.add(new Parameter("restAddress",strings[2]));
        params.add(new Parameter("restlat",strings[3]));
        params.add(new Parameter("restlng",strings[4]));
        params.add(new Parameter("restText",strings[5]));
        params.add(new Parameter("restTime",strings[6]));
        params.add(new Parameter("restPhone",strings[7]));
        String result = urlConnection.request(URL,params,activity.getApplicationContext());

        return result;
    }
}
