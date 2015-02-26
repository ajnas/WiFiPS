package com.example.indoorpositioning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.indoorpositioning.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class Settings extends Activity {
	private Button addWifi;
	WifiManager wifi;
	List<ScanResult> results;
	ListView wifisList;
	protected CharSequence[] options;
	protected boolean[] selections;
	ArrayAdapter<Router> arrayAdapter;
	ArrayList<Router> wifis;
	String building;
	Button save;

	DatabaseHelper db;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);

		setContentView(R.layout.settings);
		db = new DatabaseHelper(this);
		addWifi = (Button) findViewById(R.id.button_add);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		addWifi.setOnClickListener(new ButtonClickHandler());
		save=(Button) findViewById(R.id.save);
		
		wifisList = (ListView) findViewById(R.id.friendly_wifis);
		
		Intent intent=getIntent();
		
		building = intent.getStringExtra("BUILDING_NAME");
		wifis=db.getFriendlyWifis(building);
		arrayAdapter = new ArrayAdapter<Router>(this,
				android.R.layout.simple_list_item_1, wifis);
		// Set The Adapter
		wifisList.setAdapter(arrayAdapter);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(db.addFriendlyWifis(building,wifis))
				{
					Toast toast = Toast.makeText(getApplicationContext(),"Saved :)", Toast.LENGTH_SHORT);
					toast.show();

				}

                new FetchData().execute();
				
			}
		});
		 wifisList.setOnItemLongClickListener(new OnItemLongClickListener() {

		        @Override
		        public boolean onItemLongClick(AdapterView<?> parent, View view,
		                int arg2, long arg3) {
		        	wifis.remove(arg2);
		        	
		        	arrayAdapter.notifyDataSetChanged();
		            return false;
		        }

		    });
		 

	}

	public void updateOptions() {
		options = new CharSequence[results.size()];
		for (int i = 0; i < results.size(); i++)
			options[i] = results.get(i).SSID;
		selections = new boolean[options.length];

	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			results = wifi.getScanResults();
			updateOptions();
			
			onCreateDialog(0).show();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Choose Friendly Wifis")
				.setMultiChoiceItems(options, selections,
						new DialogSelectionClickHandler())
				.setPositiveButton("OK", new DialogButtonClickHandler())
				.create();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			Log.i("ME", options[clicked] + " selected: " + selected);
		}
	}

	public class DialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				updateFriendlyWifis();
				break;
			}
		}
	}

	protected void updateFriendlyWifis() {
		//wifis.clear();
		for (int i = 0; i < options.length; i++) {
			if (selections[i]) {
				Router router = new Router(results.get(i).SSID,
						results.get(i).BSSID);
				if (!wifis.contains(router))
					wifis.add(router);

			}
			arrayAdapter = new ArrayAdapter<Router>(this,
					android.R.layout.simple_list_item_1, wifis);
			// Set The Adapter
			wifisList.setAdapter(arrayAdapter);
			Log.i("ME", options[i] + " selected: " + selections[i]);
		}
	}

    private class FetchData extends AsyncTask<String, Integer, JSONObject> {
        private String baseUrl = "http://ajnas.in/wifips/api/";

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                return postData();
            } catch (IOException e) {
                return null;
            }


        }

        protected void onPostExecute(JSONObject json) {

            if (json == null)
            {
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
            }
            else {

                try {
                    if (json.get("result").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();



                    } else {
                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Network/Server Error", Toast.LENGTH_LONG).show();
                }
            }


        }


        protected void onProgressUpdate(Integer... progress) {

        }

        public JSONObject postData() throws IOException {
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


                    }  catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block

            } catch (IOException e) {
                // TODO Auto-generated catch block

            }


            try {
                json= "[{\"building_id\":\"331\",\"readings\":[{\"name\":\"1\",\"values\":{\"94:39:e5:2b:f8:ac\":-84,\"56:ec:99:e3:2f:08\":-85,\"e8:de:27:7d:b4:92\":-88,\"7c:e9:d3:ae:a6:ed\":-63,\"f8:1a:67:de:d4:14\":-32,\"f6:55:f9:bd:bc:4c\":-86,\"08:ed:b9:a5:b6:97\":-72,\"e8:de:27:2f:0b:ca\":-73,\"7c:c3:a1:ad:64:8e\":-68},\"MINIMUM_COMMON_ROUTERS\":1},{\"name\":\"\",\"values\":{\"cc:af:78:9e:96:f7\":-87,\"c0:14:3d:c8:39:99\":-73,\"e8:de:27:7d:b4:92\":-35,\"f8:1a:67:4d:6b:9b\":-65,\"56:db:c9:49:4b:bb\":-90,\"52:68:9d:34:60:9b\":-81},\"MINIMUM_COMMON_ROUTERS\":1}],\"friendly_wifis\":[{\"BSSID\":\"f8:1a:67:de:d4:14\",\"SSID\":\"F - Hostel\"},{\"BSSID\":\"e8:de:27:2f:0b:ca\",\"SSID\":\"Mr.47\"}]}]";
                JSONArray buildings=new JSONArray(json);
                db.updateDatabase(buildings);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;


        }

    }


}
