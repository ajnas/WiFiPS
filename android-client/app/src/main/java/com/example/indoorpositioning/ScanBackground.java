package com.example.indoorpositioning;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

/**
 * Created by Sony on 4/19/2015.
 */
public class ScanBackground extends AsyncTask<Void, Void, PositionData> {

    PositionData pos;
    private int readingCount = 30;
    private int currentCount;
    String currentPositionName;
    WifiManager wifi;
    Timer timer;
    TimerTask myTimerTask;

    private Context context;
    public ScanBackground(Context context){
        this.context=context;
    }


    protected void onPreExecute(String status) {

        Log.i("aaki", "reached pre");
        Toast.makeText(context, "Scanning started!", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPostExecute(PositionData posi) {

        Log.i("aaki", "reached post");
        Toast.makeText(context, "Done scanning !", Toast.LENGTH_LONG).show();
        this.pos=posi;
    }
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

    @Override
    protected PositionData doInBackground(Void... params) {
        Log.i("aaki", "doing");
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        currentPositionName =null;

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
        return null;
    }

    private void refresh() {
        // TODO Auto-generated method stub
        if (currentCount >= readingCount) {
            if (myTimerTask != null) {
                myTimerTask.cancel();
                returnResults();
            }
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
        Set<String> keys=positionData.values.keySet();
        for(String i: keys)
            Log.i("aaki", Integer.toString(positionData.values.get(i)));
    }

}
