package com.gen.kanazawasansaku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class SpotListActivity extends Activity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spot_list);
		
		ImageButton btnAddSpot = (ImageButton) findViewById(R.id.btnAddSpot);
		btnAddSpot.setOnClickListener(onAddSpotClick);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		SpotListFragment fragment = (SpotListFragment) getFragmentManager().findFragmentById(R.id.fragmentSpotList);
		fragment.fetchAllSpot();
	}
	
	private final OnClickListener onAddSpotClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startActivity(new Intent(SpotListActivity.this, AddSpotActivity.class));
		}
		
	};
	
}
