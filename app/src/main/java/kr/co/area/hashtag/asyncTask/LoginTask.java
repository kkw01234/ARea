package kr.co.area.hashtag.asyncTask;

import android.os.AsyncTask;

import java.util.ArrayList;

import android.app.Activity;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class LoginTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public LoginTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/login";
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("id", strings[0]));
        params.add(new Parameter("pwd", strings[1]));
        String result = conn.request(url, params, activity.getApplicationContext());
        return result;
    }
}