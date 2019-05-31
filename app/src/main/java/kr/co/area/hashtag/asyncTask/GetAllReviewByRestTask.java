package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class GetAllReviewByRestTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public GetAllReviewByRestTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) { // 0 : 식당 아이디(google_id)
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/get_all_review_by_rest"; // 아직 구현되지 않음
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("rest_id", strings[0]));
        String result = conn.request(url, params, activity.getApplicationContext());
        return result;
    }
}