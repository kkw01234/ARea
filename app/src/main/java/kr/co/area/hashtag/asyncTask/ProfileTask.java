package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class ProfileTask extends AsyncTask<Bitmap, Void, String> {
    Activity activity;
    HttpURLConnection urlConn;
    String attachmentName = "bitmap";
    String attachmentFileName = "bitmap.bmp";
    String crlf = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****BOUNDARY*****";

    public ProfileTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        // 기타 필요한 내용


        // request 준비
        urlConn = null;
        URL url = null;
        try {
            url = new URL("http://118.220.3.71:13565/upload_file");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            urlConn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        urlConn.setUseCaches(false);
        urlConn.setDoOutput(true);

        try {
            urlConn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        urlConn.setRequestProperty("Connection", "Keep-Alive");
        urlConn.setRequestProperty("Cache-Control", "no-cache");
        urlConn.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);
        // content wrapper시작
        DataOutputStream request = null;
        try {
            request = new DataOutputStream(urlConn.getOutputStream());
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bitmap을 ByteBuffer로 전환
        byte[] pixels = new byte[bitmaps[0].getWidth() * bitmaps[0].getHeight()];
        for (int i = 0; i < bitmaps[0].getWidth(); ++i) {
            for (int j = 0; j < bitmaps[0].getHeight(); ++j) {
                //we're interested only in the MSB of the first byte,
                //since the other 3 bytes are identical for B&W images
                pixels[i + j] = (byte) ((bitmaps[0].getPixel(i, j) & 0x80) >> 7);
            }
        }
        try {
            request.write(pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // content wrapper종료
        try {
            request.writeBytes(crlf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // buffer flush
        try {
            request.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            request.close();
            System.out.println("성공");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Response받기
        InputStream responseStream = null;
        try {
            responseStream = new
                    BufferedInputStream(urlConn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader responseStreamReader =
                new BufferedReader(new InputStreamReader(responseStream));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            try {
                if (!((line = responseStreamReader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append(line).append("\n");
        }
        try {
            responseStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = stringBuilder.toString();


        //Response stream종료
        try {
            responseStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // connection종료
        urlConn.disconnect();
        String result = "성공";
        return result;
    }

    private void getCookieHeader() {//Set-Cookie에 배열로 돼있는 쿠키들을 스트링 한줄로 변환
        List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");
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
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
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
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("sessionCookie", Context.MODE_PRIVATE);
        String sessionid = pref.getString("sessionid", null);
        if (sessionid != null) {
            Log.d("LOG", "세션 아이디" + sessionid + "가 요청 헤더에 포함 되었습니다.");
            urlConn.setRequestProperty("Cookie", sessionid + ";boundary=\"" + boundary);
        }
    }
}
