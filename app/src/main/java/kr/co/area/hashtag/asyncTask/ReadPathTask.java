package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class ReadPathTask extends AsyncTask<Object, Void, String> {
    private Activity activity;
    private String select;

    public ReadPathTask(Activity activity, String select){
        this.activity =activity;
        this.select = select;
    }

    @Override
    protected String doInBackground(Object... objects) {
        RequestHttpURLConnection urlConnection = new RequestHttpURLConnection();
        String result = urlConnection.request("http://118.220.3.71:13565/"+select,null,activity.getApplicationContext());
        return result;
    }
}
