package com.gen.kanazawasansaku;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.maps.model.LatLng;

public class RouteLoggingService extends Service implements LocationListener {

private final IBinder binder = new RouteLoggingBinder();
	
	private OnLocationChangedListener listener;
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// GPS‚ÌÝ’è
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_LOW);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider 		= manager.getBestProvider(criteria, true);
		manager.requestLocationUpdates(provider, 0, 0, this);
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		LatLng nowLatLng = new LatLng(
				location.getLatitude(),
				location.getLongitude());
		
		if (listener != null) listener.onLocationChangedListener(nowLatLng);
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}
	
	public void setOnLocationChangedListener (OnLocationChangedListener listener) {
		this.listener = listener;
	}
	
	/**
	 * @author yamamoto
	 *
	 */
	class RouteLoggingBinder extends Binder {
		 RouteLoggingService getService () {
			 return RouteLoggingService.this;
		 }
	}
	
	public interface OnLocationChangedListener {
		public void onLocationChangedListener(LatLng latlng);
	}

	

}
