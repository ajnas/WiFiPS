package com.example.indoorpositioning;

import java.io.Serializable;

public class Router implements Serializable{
	private String SSID;
	private String BSSID;

	public Router(String SSID, String BSSID) {
		this.SSID = SSID;
		this.BSSID = BSSID;
	}

	public String getSSID() {
		return SSID;
	}

	public String getBSSID() {
		return BSSID;
	}
	
	@Override
	public String toString(){
		return SSID;
	}
	@Override
	public boolean equals(Object arg){
		return ((Router) arg).getBSSID().equals(this.getBSSID());
	}
	
	
	@Override
	public int hashCode(){
		return  this.BSSID.hashCode();
	}
	
}