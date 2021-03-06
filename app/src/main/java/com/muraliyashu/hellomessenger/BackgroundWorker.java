package com.muraliyashu.hellomessenger;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by MuraliYashu on 8/3/2017.
 */
public class BackgroundWorker extends AsyncTask<Object,Void,Object> {
    ImageView setImage;
    Context context;
    AlertDialog dialog;
    BackgroundWorker(Context ctxt)
    {
        context = ctxt;
    }
    @Override
    protected String doInBackground(Object... params) {
        String login_url = "http://dreamapplabs.000webhostapp.com/connection.php";
        try {
            if (generalValues.checkingConnection(context)) {
                String message = (String) params[0];
                String add = (String) params[1];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = "";
                post_data = URLEncoder.encode("number", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8") + "&" +
                        URLEncoder.encode("add", "UTF-8") + "=" + URLEncoder.encode(add, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "", line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            }
            else
            {
                Toast.makeText(context,"Please check your internet connection",Toast.LENGTH_LONG).show();
            }
        }catch (MalformedURLException e)
        {
            e.printStackTrace();
            if(!generalValues.checkingConnection(context))
            {
                Toast.makeText(context,"Please check your internet connection",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            if(!generalValues.checkingConnection(context))
            {
                Toast.makeText(context,"Please check your internet connection",Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if(!generalValues.checkingConnection(context))
        {
            Toast.makeText(context,"Please check your internet connection",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
