package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class AltitudeTask extends AsyncTask<String, Void, String> {
    Activity activity;

    public AltitudeTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = "https://maps.googleapis.com/maps/api/elevation/json";
        ArrayList<Parameter> params = new ArrayList<>();
        params.add(new Parameter("locations", strings[0] + "," + strings[1]));
        params.add(new Parameter("key", activity.getResources().getString(R.string.google_maps_key)));
        RequestHttpURLConnection rc = new RequestHttpURLConnection();
        StringBuffer response = new StringBuffer();
        int result = rc.GetHttpToServer(url, params, response);

        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(response.toString());
        JsonArray resultsArray = (JsonArray) obj.get("results");
        JsonObject elejson = (JsonObject) resultsArray.get(0);
        String elevation = elejson.get("elevation").getAsString();
        return elevation;
    }
}
