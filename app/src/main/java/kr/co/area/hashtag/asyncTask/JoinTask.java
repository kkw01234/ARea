package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class JoinTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public JoinTask(Activity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/register_user";
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("id", strings[0]));
        params.add(new Parameter("pwd", strings[1]));
        params.add(new Parameter("name", strings[2]));
        params.add(new Parameter("email", strings[3]));
        String result = conn.request(url, params, activity.getApplicationContext());
        System.out.println(result);
        return result;
    }
}
