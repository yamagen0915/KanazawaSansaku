package com.gen.kanazawasansaku.apis;

import com.gen.kanazawasansaku.interfaces.OnApiResultListener;

import android.os.AsyncTask;

public class FetchAllSpotsTask extends AsyncTask<Void, Void, String> {
	
	private OnApiResultListener listener;

	@Override
	protected String doInBackground(Void... params) {
		return KanazawaSansakuAPI.fetchAllSpots();
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (listener != null) listener.onResult(result);
	}
	
	public FetchAllSpotsTask setOnApiResultListener (OnApiResultListener listener) {
		this.listener = listener;
		return this;
	}

}
