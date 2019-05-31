package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class WriteReviewTask extends AsyncTask<Object, Void, String> {
    static final String TAG = "WriteTask";
    Activity activity;
    Context context;
    HttpURLConnection httpUrlConnection = null;
    String attachmentName = "file";
    String attachmentFileName = "profile.jpg";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";


    public WriteReviewTask(Activity activity) {
        this.activity = activity;
        context = activity.getApplicationContext();
    }

    @Override
    protected String doInBackground(Object... objects) { // 0 : bmp 이미지파일 , 1 : rest_id, 2 : content, 3 : rate
        Bitmap bitmap = (Bitmap) objects[0];
        String rest_id = (String) objects[1];
        String content = (String) objects[2];
        String rate = (String) objects[3];
        attachmentFileName = rest_id + ".jpg";
        try {
            URL url = new URL("http://118.220.3.71:13565/create_review");
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + this.boundary);
            setCookieHeader();
            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(this.twoHyphens + boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"category\"" + this.crlf);
            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf + this.crlf);
            request.writeBytes("upload_review_image" + this.crlf);

            request.writeBytes(this.twoHyphens + boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"google_id\"" + this.crlf);
            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf + this.crlf);
            request.writeBytes(rest_id + this.crlf);

            request.writeBytes(this.twoHyphens + boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"review_content\"" + this.crlf);
            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf + this.crlf);
            request.write(content.getBytes("UTF-8"));
            request.writeBytes(this.crlf);

            request.writeBytes(this.twoHyphens + boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"review_rate\"" + this.crlf);
            request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf + this.crlf);
            request.writeBytes(rate + this.crlf);

            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    this.attachmentName + "\";filename=\"" +
                    this.attachmentFileName + "\"" + this.crlf);
            request.writeBytes("Content-Transfer-Encoding: binary" + this.crlf);
            request.writeBytes(this.crlf);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            request.write(byteArray);
            request.writeBytes(this.crlf);
            request.writeBytes(this.twoHyphens + this.boundary +
                    this.twoHyphens + this.crlf);
            request.flush();
            request.close();

            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            getCookieHeader();
            responseStreamReader.close();
            String response = stringBuilder.toString();

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getCookieHeader() {//Set-Cookie에 배열로 돼있는 쿠키들을 스트링 한줄로 변환
        List<String> cookies = httpUrlConnection.getHeaderFields().get("Set-Cookie");
        //cookies -> [JSESSIONID=D3F829CE262BC65853F851F6549C7F3E; Path=/smartudy; HttpOnly] -> []가 쿠키1개임.
        //Path -> 쿠키가 유효한 경로 ,/smartudy의 하위 경로에 위의 쿠키를 사용 가능.
        if (cookies != null) {
            for (String cookie : cookies) {
                String sessionid = cookie.split(";\\s*")[0];
                //JSESSIONID=FB42C80FC3428ABBEF185C24DBBF6C40를 얻음.
                //세션아이디가 포함된 쿠키를 얻었음.
                setSessionIdInSharedPref(sessionid);

            }
        }
    }


    private void setSessionIdInSharedPref(String sessionid) {
        SharedPreferences pref = context.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        if (pref.getString("sessionid", null) == null) { //처음 로그인하여 세션아이디를 받은 경우
            Log.d("LOG", "처음 로그인하여 세션 아이디를 pref에 넣었습니다." + sessionid);
        } else if (!pref.getString("sessionid", null).equals(sessionid)) { //서버의 세션 아이디 만료 후 갱신된 아이디가 수신된경우
            Log.d("LOG", "기존의 세션 아이디" + pref.getString("sessionid", null) + "가 만료 되어서 "
                    + "서버의 세션 아이디 " + sessionid + " 로 교체 되었습니다.");
        }
        edit.putString("sessionid", sessionid);
        edit.apply(); //비동기 처리
    }


    private void setCookieHeader() {
        SharedPreferences pref = context.getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
        String sessionid = pref.getString("sessionid", null);
        if (sessionid != null) {
            Log.d("LOG", "세션 아이디" + sessionid + "가 요청 헤더에 포함 되었습니다.");
            httpUrlConnection.setRequestProperty("Cookie", sessionid);
        }
    }
}