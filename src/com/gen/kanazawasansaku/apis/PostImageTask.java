package com.gen.kanazawasansaku.apis;

import android.os.AsyncTask;

import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Spot;
import com.gen.kanazawasansaku.interfaces.OnApiResultListener;

public class PostImageTask extends AsyncTask<Spot, Void, String> {
	
	
	private OnApiResultListener listener;

	@Override
	protected String doInBackground(Spot... params) {
		
		if (params.length <= 0) return "";
		
		Spot spot = params[0];
		
		return KanazawaSansakuAPI.postImage(
				"spot" + spot.getId() + ".jpg", 
				spot.getPicture());
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if (listener != null) listener.onResult(result);
		
	}
	
	public PostImageTask setOnApiResultListener (OnApiResultListener listener) {
		this.listener = listener;
		return this;
	}
	

	
}
