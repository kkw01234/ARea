package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

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

import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class ProfileTask extends AsyncTask<Bitmap,Void,String> {
    Activity activity;

    public ProfileTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        // 기타 필요한 내용
        String attachmentName = "bitmap";
        String attachmentFileName = "bitmap.bmp";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

// request 준비
        HttpURLConnection httpUrlConnection = null;
        URL url = null;
        try {
            url = new URL("http://118.220.3.71:13565/upload_file");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setDoOutput(true);

        try {
            httpUrlConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
        httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
        httpUrlConnection.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);

// content wrapper시작
        DataOutputStream request = null;
        try {
            request = new DataOutputStream(httpUrlConnection.getOutputStream());
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
                    BufferedInputStream(httpUrlConnection.getInputStream());
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
        httpUrlConnection.disconnect();
        String result = "성공";
        return result;
    }
}
