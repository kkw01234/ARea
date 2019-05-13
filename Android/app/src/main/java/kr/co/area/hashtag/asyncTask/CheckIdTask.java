package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class CheckIdTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public CheckIdTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/check_dup_id";
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("id", strings[0]));
        String result = conn.request(url, params, activity.getApplicationContext());
        System.out.println(result);
        return result;
    }
}