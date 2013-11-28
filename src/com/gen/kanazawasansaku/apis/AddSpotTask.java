package com.gen.kanazawasansaku.apis;

import android.os.AsyncTask;

import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Spot;
import com.gen.kanazawasansaku.interfaces.OnApiResultListener;

public class AddSpotTask extends AsyncTask<Spot, Void, String> {
	
	private OnApiResultListener listener;

	@Override
	protected String doInBackground(Spot... spots) {
		return KanazawaSansakuAPI.uploadSpots(spots);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (listener != null) listener.onResult(result);
		
	}
	
	public AddSpotTask setOnApiResultListener(OnApiResultListener listener) {
		this.listener = listener;
		return this;
	}
	
	

}
