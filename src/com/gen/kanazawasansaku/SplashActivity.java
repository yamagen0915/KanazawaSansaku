package com.gen.kanazawasansaku;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

public class SplashActivity extends FragmentActivity implements Runnable {
	
private static final int DELAY_TIME_MILLIS = 2 * 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(this, DELAY_TIME_MILLIS);
	}

	@Override
	public void run () {
		startActivity(new Intent(this, SpotListActivity.class));
		finish();
	}

}
