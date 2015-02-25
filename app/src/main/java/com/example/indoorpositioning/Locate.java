package com.example.indoorpositioning;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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

}
