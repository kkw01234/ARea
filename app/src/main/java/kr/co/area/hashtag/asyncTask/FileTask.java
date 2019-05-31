package kr.co.area.hashtag.asyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FileTask extends AsyncTask<File, Void, String> {
    NetworkInfo net;

    Activity activity;

    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    DataInputStream inputStream = null;

    String folderPath;
    String arrayOfFiles[];
    File root;
    File allFiles;

    String urlServer = "http://118.220.3.71:13565/upload_file";
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";

    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1*1024*1024;

    URL url;


    public FileTask(Activity activity){
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {


    }

    @Override
    protected String doInBackground(File... params) {

        Log.d(" UploadGpsData","doInBackground");

            //File filename = new File(arrayOfFiles[i].toString());
            try {
                url = new URL(urlServer);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            try {
                connection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            try{

                FileInputStream fileInputStream = new FileInputStream(params[0]);
                try {
                    outputStream = new DataOutputStream( connection.getOutputStream() );
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + folderPath+params[0].getName()+"\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0){
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //int serverResponseCode = connection.getResponseCode();
                //String serverResponseMessage = connection.getResponseMessage();

                // Responses from the server (code and message)
                //serverResponseCode = connection.getResponseCode();
                //serverResponseMessage = connection.getResponseMessage();

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch(Exception e){
                e.printStackTrace();
            }


        System.out.println("??????????????????");

        return null;
    }


    protected void onPostExecute(Void result) {

        Log.d(" UploadGpsData","onPost");

    }
}

