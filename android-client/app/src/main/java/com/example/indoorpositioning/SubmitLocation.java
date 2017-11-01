package com.example.indoorpositioning;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubmitLocation extends AsyncTask<String, Integer, JSONObject> {
        private String baseUrl = Config.BASE_URL;
        private Context context;
        public SubmitLocation(Context context){
            this.context=context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                return postData(params[0]);
            } catch (IOException e) {
                return null;
            }


        }

        protected void onPostExecute(JSONObject json) {

            if (json == null)
            {
                Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
            }
            else {

                try {
                    if (json.get("result").equals("success")) {
                        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();



                    } else {
                        Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Network/Server Error", Toast.LENGTH_LONG).show();
                }
            }


        }


        protected void onProgressUpdate(Integer... progress) {

        }

        public JSONObject postData(String location) throws IOException {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(baseUrl + "submit");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();

                String mac = info.getMacAddress();
                nameValuePairs.add(new BasicNameValuePair("mac", mac));
                nameValuePairs.add(new BasicNameValuePair("building_id",location));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                JSONObject json = null;
                if (response == null)
                    return null;
                else {
                    try {
                        json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    } catch (JSONException e) {

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return json;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

    }