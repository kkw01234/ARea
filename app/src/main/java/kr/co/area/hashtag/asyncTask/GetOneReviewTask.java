package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class GetOneReviewTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public GetOneReviewTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) { // 0 : 리뷰 아이디
        RequestHttpURLConnection conn = new RequestHttpURLConnection();
        String url = "http://118.220.3.71:13565/get_one_review"; // 아직 구현되지 않음
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("review_id", strings[0]));
        String result = conn.request(url, params, activity.getApplicationContext());
        return result;
    }
}