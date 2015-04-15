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

public class FriendlyWifis extends Activity {
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






}
