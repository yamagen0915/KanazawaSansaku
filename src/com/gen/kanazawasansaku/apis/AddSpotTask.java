package com.gen.kanazawasansaku.apis;

import android.os.AsyncTask;

import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Spot;

public class AddSpotTask extends AsyncTask<Spot, Void, Void> {

	@Override
	protected Void doInBackground(Spot... spots) {
		KanazawaSansakuAPI.uploadSpots(spots);
		return null;
	}


}
