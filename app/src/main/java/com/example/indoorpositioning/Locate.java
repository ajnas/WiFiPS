package com.example.indoorpositioning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;

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

public class Locate extends Activity {

	ArrayList<String> buildings;
	DatabaseHelper db;
	ArrayAdapter<String> arrayAdapter;
	ArrayList<PositionData> positionsData;
	String building;
	TextView result;
	//Spinner buildingSpinner;
	Button locate;
	ImageView locationImage;
	LinearLayout everyThingElse;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.locate);
		db = new DatabaseHelper(this);
		buildings = db.getBuildings();
		locate = (Button) findViewById(R.id.locate);
		//buildingSpinner = (Spinner) findViewById(R.id.buildingspinner);
		everyThingElse=(LinearLayout) findViewById(R.id.everythingelse);
		locationImage=(ImageView)findViewById(R.id.locationimage);
		result = (TextView) findViewById(R.id.result);
		// Create The Adapter with passing ArrayList as 3rd parameter
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, buildings);
	/*	arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		buildingSpinner.setAdapter(arrayAdapter);
		buildingSpinner
				.setOnItemSelectedListener(new CustomOnItemSelectedListener());
				*/
		locate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), Scan.class);
				intent.putExtra("isLearning", false);
				startActivityForResult(intent,0);
				
			}
		});
	
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, buildings);
		// Set The Adapter

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose building");
		builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// the user clicked on colors[which]
				building = buildings.get(which);
				setImage(building);
							
				
			}
		});
		builder.show();

	}

    @Override
    protected void onResume() {
        new FetchData(this).execute();
        super.onResume();
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		PositionData positionData = (PositionData) intent
				.getSerializableExtra("PositionData");
		positionsData=db.getReadings(building);

		String closestPosition = null;
		ArrayList<Router> wifis = db.getFriendlyWifis(building);

		int min_distance = positionData.uDistance(positionsData.get(0), wifis);
		closestPosition = positionsData.get(0).getName();
		String res = "";
		res += closestPosition + "\n" + min_distance;
		res += "\n" + positionsData.get(0).toString();
		for (int i = 1; i < positionsData.size(); i++) {
			int distance = positionData.uDistance(positionsData.get(i), wifis);
			res += "\n" + positionsData.get(i).getName() + "\n" + distance;
			res += "\n" + positionsData.get(i).toString();
			if (distance <= min_distance) {
				min_distance = distance;

				closestPosition = positionsData.get(i).getName();

			}

		}
		if (min_distance == PositionData.MAX_DISTANCE){
			closestPosition = "OUTOFRANGE";
			setImage(building);
		}
		else
			setImage(building+"_"+closestPosition);
		
			
		res += "\nCurrent:\n" + positionData.toString();
		Log.v("Result",res);
		
		result.setText("You are at " + closestPosition + "\n" + res);
		//everyThingElse.setVisibility(LinearLayout.GONE);
		
		super.onActivityResult(requestCode, resultCode, intent);
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
			Intent intent = new Intent(this, Settings.class);
			intent.putExtra("BUILDING_NAME", building);
			startActivity(intent);
		} else if (id == R.id.take_readings) {
			Intent intent = new Intent(this, Buildings.class);
			startActivity(intent);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public class CustomOnItemSelectedListener implements
			AdapterView.OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			building = parent.getItemAtPosition(pos).toString();
			locate.setEnabled(true);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			locate.setEnabled(false);
		}

	}
	public void setImage(String imageName){
		int resourceId = getResources().getIdentifier(
				   imageName, "drawable", getPackageName() );
		locationImage.setImageResource(resourceId);
		
	}



    private class SubmitLocation extends AsyncTask<String, Integer, JSONObject> {
        private String baseUrl = "";

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

        public JSONObject postData(String location) throws IOException {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(baseUrl + "submit");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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

}
