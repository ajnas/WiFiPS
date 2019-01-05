package com.example.indoorpositioning;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Scan extends Activity {

	private TextView warning;
	private TextView timeRemaining;
	private Button calibrate;
	private int readingCount = 30;
	private int currentCount;
	String currentPositionName;
	WifiManager wifi;
	Timer timer;
	TimerTask myTimerTask;
	
	public class ResultData {
		private Router router;

		public Router getRouter() {
			return this.router;
		}

		public List<Integer> values;

		public ResultData(Router router) {
			// TODO Auto-generated constructor stub
			this.router = router;
			values = new ArrayList<Integer>();
		}
	}

	private List<ResultData> resultsData;
	private List<PositionData> positionsData;
	private PositionData positionData;

	@SuppressWarnings("null")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// System.out.println("test");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan);
		warning = (TextView) findViewById(R.id.TextView1);
		timeRemaining = (TextView) findViewById(R.id.TextView2);
		calibrate = (Button) findViewById(R.id.start);
		
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		Intent intent = getIntent();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm...");
        alertDialog.setMessage("Scanning requires WiFi.");
        alertDialog.setPositiveButton("Turn on WiFi",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Activity transfer to wifi settings
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        alertDialog.setCancelable(false);
        if(!wifi.isWifiEnabled()) {
            alertDialog.show();
        }

		currentPositionName =null;
		if(intent.getBooleanExtra("isLearning",true))
			currentPositionName = intent.getStringExtra("POSITION_NAME");
		
		calibrate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				calibrate.setEnabled(false);
				warning.setText("DO NOT MOVE FOR");
				resultsData = new ArrayList<ResultData>();
				currentCount = 0;
				timer = new Timer();
				myTimerTask = new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						refresh();
					}
				};
				timer.schedule(myTimerTask, 0, 1000);
			}
		});

	}

	private void refresh() {
		// TODO Auto-generated method stub
		if (currentCount >= readingCount) {
			if (myTimerTask != null)
				myTimerTask.cancel();

		}
		currentCount++;
		wifi.startScan();
		List<ScanResult> results = wifi.getScanResults();
		for (int i = 0; i < results.size(); i++) {
			// System.out.println("test2");
			String ssid0 = results.get(i).SSID;
			String bssid = results.get(i).BSSID;

			int rssi0 = results.get(i).level;
			boolean found = false;
			for (int pos = 0; pos < resultsData.size(); pos++) {
				if (resultsData.get(pos).getRouter().getBSSID().equals(bssid)) {
					found = true;
					resultsData.get(pos).values.add(rssi0);
					break;
				}
			}
			if (!found) {

				ResultData data = new ResultData(new Router(ssid0, bssid));
				data.values.add(rssi0);
				resultsData.add(data);
			}
			// String rssiString0 = String.valueOf(rssi0);
			// textStatus = textStatus.concat("\n" + ssid0 + "   " +
			// rssiString0);
			// System.out.println("ajsdhks"+textStatus);
		}
		// Log.v("textStatus", textStatus);
		// System.out.println(""+textStatus);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// result.setText("here"+currentCount);
				timeRemaining
						.setText(" " + (readingCount - currentCount) + "s");
				if (currentCount >= readingCount) {
					returnResults();
				}
			}
		});

	}

	private void returnResults() {
		// TODO Auto-generated method stub

		positionData = new PositionData(currentPositionName);
		for (int length = 0; length < resultsData.size(); length++) {

			int sum = 0;
			for (int l = 0; l < resultsData.get(length).values.size(); l++) {
				sum += resultsData.get(length).values.get(l);

			}
			int average = sum / resultsData.get(length).values.size();

			positionData.addValue(resultsData.get(length).getRouter(), average);
		}

		Intent intent = new Intent(getApplicationContext(), Positions.class);
		intent.putExtra("PositionData", (Serializable) positionData);
		setResult(RESULT_OK,intent);
		finish();
		

	}



}
