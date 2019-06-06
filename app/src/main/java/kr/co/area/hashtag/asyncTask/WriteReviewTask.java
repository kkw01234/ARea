package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class WriteReviewTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public WriteReviewTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) { // 0 : rest_id, 1 : content, 2 : rate
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/create_review";
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("google_id", strings[0]));
        params.add(new Parameter("review_content", strings[1]));
        params.add(new Parameter("review_rate", strings[2]));
        String result = conn.request(url, params, activity.getApplicationContext());
        System.out.println(result);
        return result;
    }
}
