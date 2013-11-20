package com.gen.kanazawasansaku;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity implements Runnable {
	
private static final int DELAY_TIME_MILLIS = 2 * 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// DELAY_TIME_MILLISŒã‚É‰æ–Ê‘JˆÚ‚·‚é
		new Handler().postDelayed(this, DELAY_TIME_MILLIS);
	}

	@Override
	public void run () {
		startActivity(new Intent(this, RouteListActivity.class));
		finish();
	}

}
