package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class LogoutTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public LogoutTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/logout";
        String result = conn.request(url, null, activity.getApplicationContext());
        System.out.println(result);
        SharedPreferences auto = activity.getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = auto.edit();
        editor.clear();
        editor.commit();
        return result;
    }
}
