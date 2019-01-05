package com.example.indoorpositioning;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PositionData implements Serializable {
	public static final int MAX_DISTANCE=99999999;
	private String name;
	public static final int MINIMUM_COMMON_ROUTERS=1;
	public HashMap<String, Integer> values;
    public HashMap<String,String> routers;
	public PositionData(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
		values = new HashMap<String, Integer>();
        routers = new HashMap<String, String>();

	}
	public void addValue(Router router,int strength){

		values.put(router.getBSSID(), strength);
        routers.put(router.getBSSID(),router.getSSID());

	}
	public String getName() {
		return name;
	}
	public String toString() {
		String result="";
		result+=name+"\n";
		for(Map.Entry<String, Integer> e: this.values.entrySet())
				 result+=routers.get(e.getKey())+" : "+e.getValue().toString()+"\n";
		
		return result; 	
		
	}
	public HashMap<String, Integer> getValues() {
		return values;
	}

	public int uDistance(PositionData arg,ArrayList<Router> friendlyWifis){
		int sum=0;
		int count=0;
		 for(Map.Entry<String, Integer> e: this.values.entrySet()){
			 int v;
			//Log.v("Key : ",arg.values.get(e.getKey()).toString());
			 if(isFriendlyWifi(friendlyWifis,e.getKey()) && arg.values.containsKey(e.getKey()))
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

    private boolean isFriendlyWifi(ArrayList<Router> wifis,String bssid){
        for(int i=0;i<wifis.size();i++){
            if(wifis.get(i).getBSSID().equals(bssid))
                return true;

        }
        return false;

    }
	
}
