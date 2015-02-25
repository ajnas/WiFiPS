package com.example.indoorpositioning;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class PositionData implements Serializable {
	public static final int MAX_DISTANCE=99999999;
	private String name;
	private int MINIMUM_COMMON_ROUTERS=1;
	public HashMap<Router, Integer> values;
	public PositionData(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
		values = new HashMap<Router, Integer>();
	}
	public void addValue(Router router,int strength){
		
		
		values.put(router, strength);
	}
	public String getName() {
		return name;
	}
	public String toString() {
		String result="";
		result+=name+"\n";
		for(Map.Entry<Router, Integer> e: this.values.entrySet())
				 result+=e.getKey().getSSID()+" : "+e.getValue().toString()+"\n";
		
		return result; 	
		
	}
	public HashMap<Router, Integer> getValues() {
		return values;
	}

	public int uDistance(PositionData arg,ArrayList<Router> friendlyWifis){
		int sum=0;
		int count=0;
		 for(Map.Entry<Router, Integer> e: this.values.entrySet()){
			 int v;
			//Log.v("Key : ",arg.values.get(e.getKey()).toString());
			 if(friendlyWifis.contains(e.getKey()) && arg.values.containsKey(e.getKey()))
				 {
				  v=arg.values.get(e.getKey());
				  sum+=Math.pow((v-e.getValue()),2);
				  count++;
				 }			 			 		 
			}
		 if(count<MINIMUM_COMMON_ROUTERS){
			 sum=MAX_DISTANCE;
		 }
		 
		 return sum;
	}
	
}
