package com.gen.kanazawasansaku;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class SpotListActivity extends FragmentActivity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spot_list);
		
		ImageButton btnAddSpot = (ImageButton) findViewById(R.id.btnAddSpot);
		btnAddSpot.setOnClickListener(onAddStopClick);
	}
	
	private final OnClickListener onAddStopClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startActivity(new Intent(SpotListActivity.this, AddSpotActivity.class));
		}
		
	};
	
	private static class MyPagerAdapter extends FragmentStatePagerAdapter {
		
		private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		private void addFragment (Fragment fragment) {
			this.fragments.add(fragment);
		} 

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position == 0) return "‚¨‚·‚·‚ß";
			else 			   return "MyRoute";
		}
	}

}
