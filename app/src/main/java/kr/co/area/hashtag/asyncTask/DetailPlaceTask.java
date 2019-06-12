package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class DetailPlaceTask extends AsyncTask<String, Void, String> {
    Activity activity;
    public DetailPlaceTask(Activity activity){
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "https://maps.googleapis.com/maps/api/place/details/json";
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("placeid",strings[0]));
        params.add(new Parameter("key",activity.getResources().getString(R.string.google_maps_key)));
        StringBuffer builder = new StringBuffer();
        int result = conn.GetHttpToServer(url, params,builder,"GET");
        System.out.println(result);
        return builder.toString();

    }
}
