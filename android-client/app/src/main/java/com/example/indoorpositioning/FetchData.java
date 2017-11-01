package com.example.indoorpositioning;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FetchData extends AsyncTask<String, Integer, String> {
    private String baseUrl = Config.BASE_URL;
    private Context context;
    public FetchData(Context context){
        this.context=context;
    }
    @Override
    protected String doInBackground(String... params) {

        return postData();


    }

    protected void onPostExecute(String status) {

            Toast.makeText(context,status, Toast.LENGTH_LONG).show();

    }


    protected void onProgressUpdate(Integer... progress) {

    }

    public String postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(baseUrl + "");
        String json = null;

        try {
            // Add your data

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpGet);

            if (response != null) {
                try {
                    json = EntityUtils.toString(response.getEntity());
                    Log.d("Fetch Data", json);
                    JSONArray buildings = new JSONArray(json);
                    DatabaseHelper db=new DatabaseHelper(context);
                    db.updateDatabase(buildings);
                    return "Db Updated";


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block

        } catch (IOException e) {
            // TODO Auto-generated catch block

        }


        return "Error";


    }

}