package com.gen.kanazawasansaku;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.gen.kanazawasansaku.DbAccess.Route;
import com.gen.kanazawasansaku.RouteListFragment.OnSelectRouteListener;

public class RouteListActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_list);
		
		// Ç®Ç∑Ç∑ÇﬂÇ∆é©ï™ÇÃãLò^ÇµÇΩåoòHÇï\é¶Ç∑ÇÈÅB
		MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
		for (String routeType : new String[]{"MyRoute", "Recomend"}) {
			Bundle param = new Bundle();
			param.putString("routeType", routeType);
			RouteListFragment listFragment = new RouteListFragment();
			listFragment.setOnSelectRouteListener(onSelectRouteListener);
			listFragment.setArguments(param);
			adapter.addFragment(listFragment);
		}
		
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(adapter);
		
		ImageButton btnStartLog = (ImageButton) findViewById(R.id.btnStartLog);
		btnStartLog.setOnClickListener(onStartLogClick);
	}
	
	private final OnClickListener onStartLogClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RouteListActivity.this, MapActivity.class);
			intent.putExtra("isLogging", true);
			startActivity(intent);
		}
		
	};
	
	private final OnSelectRouteListener onSelectRouteListener = new OnSelectRouteListener() {
		
		@Override
		public void onSelected(Route route) {
			Intent intent = new Intent(RouteListActivity.this, MapActivity.class);
			intent.putExtra("routeId", route.getId());
			intent.putExtra("selectRoute", route);
			startActivity(intent);
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
			if (position == 0) return "Ç®Ç∑Ç∑Çﬂ";
			else 			   return "MyRoute";
		}
	}

}
