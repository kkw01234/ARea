package kr.co.area.hashtag.asyncTask;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class SimilarTask extends AsyncTask<Object, Void, Integer> {
    @Override
    protected Integer doInBackground(Object... objects) {

        String key = "95apduobleerstcy97"; //암호문
        String user_id = (String)objects[0];
        String rest_google_id = (String)objects[1];
        String url = "http://118.220.3.71:14565/similar";
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("key", key));
        params.add(new Parameter("user_id", user_id));
        params.add(new Parameter("google_id",rest_google_id));
        RequestHttpURLConnection rc = new RequestHttpURLConnection();
        StringBuffer response = new StringBuffer();
        int result = rc.GetHttpToServer(url, params, response,"GET");
        String str =response.toString();
        System.out.println(str);
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject)parser.parse(str);
        int re = 0;
        try{
            re = (int)object.get("result").getAsDouble();

        }catch (Exception e){
            e.printStackTrace();
            re = 0;

        }
        return re;

    }
}
