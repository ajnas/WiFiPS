package com.example.indoorpositioning;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.text.style.BulletSpan;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Positions extends Activity {

	private TextView textHeading;
	private Button calibrate;
	private Button finish;
	private EditText placeName;
	private int readingCount = 30;
	private TextView results;
	private String resultsText;
	private ListView positionsList;
	ArrayList<String> positions;
	ArrayAdapter arrayAdapter;
	Timer timer;
	TimerTask myTimerTask;
	int positionCount;
	DatabaseHelper db;
	private Boolean isLearning= true;
	

	private List<PositionData> positionsData;
	private PositionData positionData;
	private String building;
    Gson gson ;

    @SuppressWarnings("null")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// System.out.println("test");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.positions);
		textHeading = (TextView) findViewById(R.id.textHeading);
		placeName = (EditText) findViewById(R.id.placename);
		calibrate = (Button) findViewById(R.id.calibratebutton);
		// results= (TextView) findViewById(R.id.results);
		finish = (Button) findViewById(R.id.finish);
		positionsList = (ListView) findViewById(R.id.positionslist);
        gson=new Gson();
		resultsText = "";

		positionCount = 0;
		positionsData = new ArrayList<PositionData>();
		Intent intent = getIntent();
		// readingCount = Integer.parseInt(intent
		// .getStringExtra("NUMBER_OF_SECONDS"));
		building = intent.getStringExtra("BUILDING_NAME");
		db = new DatabaseHelper(this);
		positions = db.getPositions(building);
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, positions);
		positionsList.setAdapter(arrayAdapter);

		calibrate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), Scan.class);
				intent.putExtra("PLACE_NAME", placeName.getText().toString());
				intent.putExtra("isLearning",isLearning);
				intent.putExtra("NUMBER_OF_SECONDS", readingCount);
				startActivityForResult(intent, 0);
			}
		});
		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Buildings.class);
				setResult(2, intent);
                ArrayList<PositionData> buildingReadings=db.getReadings(building);
                ArrayList<Router> friendlyWifis=db.getFriendlyWifis(building);
                String buildingReadingsJson=gson.toJson(buildingReadings);
                String friendlyWifisJson= gson.toJson(friendlyWifis);
                JSONObject json=new JSONObject();
                try {
                    json.accumulate("building_id",building);
                    json.accumulate("readings",new JSONArray(buildingReadingsJson));
                    json.accumulate("friendly_wifis",new JSONArray(friendlyWifisJson));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Submit(getApplicationContext()).execute(json.toString());
				finish();

			}
		});
		positionsList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {
						Intent intent = new Intent(getApplicationContext(),
								Scan.class);
						String selectedPosition = (String) parent
								.getItemAtPosition(position);
						intent.putExtra("isLearning",isLearning);
						intent.putExtra("PLACE_NAME", selectedPosition);
						intent.putExtra("NUMBER_OF_SECONDS", readingCount);
						startActivityForResult(intent, 0);
					}
				});
		 positionsList.setOnItemLongClickListener(new OnItemLongClickListener() {

		        @Override
		        public boolean onItemLongClick(AdapterView<?> parent, View view,
		                int arg2, long arg3) {
		        	db.deleteReading(building,positions.get(arg2));
		        	positions.remove(arg2);
		        	
		        	arrayAdapter.notifyDataSetChanged();
		            return false;
		        }

		    });
		
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		// TODO Auto-generated method stub
		positionData = (PositionData) intent
				.getSerializableExtra("PositionData");
		Log.v("Before db : ", positionData.toString());
		db.addReadings(building, positionData);
		positions = db.getPositions(building);
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, positions);
		positionsList.setAdapter(arrayAdapter);
		// resultsText+=db.getReadings("first").get(0).toString();
		// results.setText(resultsText);

		super.onActivityResult(arg0, arg1, intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent= new Intent(this,Settings.class);
			intent.putExtra("BUILDING_NAME",building);
			startActivity(intent);
		}
		else if(id== R.id.take_readings){
			Intent intent= new Intent(this,Positions.class);
			intent.putExtra("BUILDING_NAME",building);
			startActivity(intent);
			
			return true;
		}

	
	return super.onOptionsItemSelected(item);
	}



}
