package com.example.indoorpositioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "wifips.db";
	public static final String AP_TABLE = "access_points";
	public static final String READINGS_TABLE = "readings";
	public static final String AP_CREATE = "CREATE TABLE 'access_points' "
			+ "('building_id' TEXT NOT NULL ,'ssid' TEXT NOT NULL,'mac_id' TEXT NOT NULL )";
	public static final String READINGS_CREATE = "CREATE TABLE 'readings' ('building_id' TEXT NOT NULL , "
			+ "'position_id' TEXT NOT NULL ,"
			+ " 'ssid' TEXT NOT NULL , 'mac_id' TEXT NOT NULL , 'rssi' INTEGER NOT NULL )";

	private HashMap hp;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(AP_CREATE);
		db.execSQL(READINGS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + AP_CREATE);
		db.execSQL("DROP TABLE IF EXISTS " + READINGS_CREATE);
		onCreate(db);
	}

	public int deleteReading(String building_id, String position_id) {
		SQLiteDatabase db = getWritableDatabase();
		String[] args = new String[] { building_id, position_id };
		return db.delete(READINGS_TABLE, "building_id=? and position_id=?",
				args);

	}


	public boolean deleteBuilding(String building_id) {
		SQLiteDatabase db = getWritableDatabase();
		String[] args = new String[] { building_id };
		db.delete(AP_TABLE,"building_id=?",args);
		db.delete(READINGS_TABLE, "building_id=?", args);
		return true;

	}

	public ArrayList<String> getBuildings() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select distinct building_id from "
				+ READINGS_TABLE, null);
		ArrayList<String> result = new ArrayList<String>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			result.add(cursor.getString(0));
			cursor.moveToNext();
		}
		return result;

	}

	public ArrayList<Router> getFriendlyWifis(String building_id) {
		ArrayList<Router> result = new ArrayList<Router>();
		System.out.println(building_id);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select ssid,mac_id from " + AP_TABLE
				+ " where building_id=?", new String[] { building_id });
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			result.add(new Router(cursor.getString(0), cursor.getString(1)));
			cursor.moveToNext();
		}
		return result;

	}

	public int deleteFriendlyWifis(String building_id) {
		SQLiteDatabase db = getWritableDatabase();
		String[] args = new String[] { building_id };
		return db.delete(AP_TABLE, "building_id=?", args);

	}

	public boolean addFriendlyWifis(String building_id, ArrayList<Router> wifis) {
		deleteFriendlyWifis(building_id);
		SQLiteDatabase db = getWritableDatabase();
		for (int i = 0; i < wifis.size(); i++) {
			ContentValues cv = new ContentValues();
			cv.put("building_id", building_id);
			cv.put("ssid", wifis.get(i).getSSID());
			cv.put("mac_id", wifis.get(i).getBSSID());
			db.insert(AP_TABLE, null, cv);
		}
		System.out.println("Adding done");
		return true;
	}

	public ArrayList<String> getPositions(String building_id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select distinct position_id from "
				+ READINGS_TABLE + " where building_id=?",
				new String[] { building_id });
		ArrayList<String> result = new ArrayList<String>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			result.add(cursor.getString(0));
			cursor.moveToNext();
		}
		return result;
	}

	public boolean addReadings(String building_id, PositionData positionData) {
		Log.v("Just Before db : ", positionData.toString());
		deleteReading(building_id, positionData.getName());

		SQLiteDatabase db = getWritableDatabase();
		for (Map.Entry<String, Integer> e : positionData.getValues().entrySet()) {
			ContentValues cv = new ContentValues();
			cv.put("building_id", building_id);
			cv.put("position_id", positionData.getName());
			cv.put("ssid",positionData.routers.get(e.getKey()));
			cv.put("mac_id",e.getKey());
			cv.put("rssi", e.getValue());
			Log.v(e.getKey(), e.getValue().toString());
			db.insert(READINGS_TABLE, null, cv);
		}
		System.out.println("Adding done");
		return true;

	}

    public boolean updateDatabase(JSONArray buildings) throws JSONException {
        Gson gson=new Gson();

        for(int i=0;i<buildings.length();i++){
            JSONObject building=buildings.getJSONObject(i);
            String building_id=building.getString("building_id");

            ArrayList<PositionData> readings= null;
            ArrayList<Router> friendlyWifis=null;



            try {
               Log.d("Readings",building.get("readings").toString());

                readings = gson.fromJson(building.get("readings").toString(),new TypeToken<ArrayList<PositionData>>() {
                }.getType());
                friendlyWifis=gson.fromJson(building.get("friendly_wifis").toString()
                        ,new TypeToken<ArrayList<Router>>() {
                }.getType());
                deleteBuilding(building_id);
                for(int j=0;j<readings.size();j++){
                    addReadings(building.getString("building_id"),readings.get(j));
                }
                addFriendlyWifis(building.getString("building_id"),friendlyWifis);

            } catch (JSONException e) {
                return false;
            }



        }
        return true;

    }


	public ArrayList<PositionData> getReadings(String building_id) {
		HashMap<String, PositionData> positions = new HashMap<String, PositionData>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select distinct * from " + READINGS_TABLE
				+ " where building_id='" + building_id + "'", null);
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			String position_id = cursor.getString(1);
			Router router = new Router(cursor.getString(2), cursor.getString(3));
			Log.v(cursor.getString(2), cursor.getInt(4) + "");
			if (positions.containsKey(position_id)) {

				positions.get(position_id).addValue(router, cursor.getInt(4));
			} else {
				PositionData positionData = new PositionData(
						cursor.getString(1));
				positionData.addValue(router, cursor.getInt(4));
				positions.put(position_id, positionData);
			}
			cursor.moveToNext();

		}
		System.out.println("Reading done");
		ArrayList<PositionData> result = new ArrayList<PositionData>();
		for (Map.Entry<String, PositionData> e : positions.entrySet())
			result.add(e.getValue());
		return result;

	}
}
