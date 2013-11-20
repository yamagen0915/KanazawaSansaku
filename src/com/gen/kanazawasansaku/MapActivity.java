package com.gen.kanazawasansaku;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gen.kanazawasansaku.DbAccess.Route;
import com.gen.kanazawasansaku.RegistRouteDialog.OnRegistRouteListener;
import com.gen.kanazawasansaku.RouteLoggingService.OnLocationChangedListener;
import com.gen.kanazawasansaku.RouteLoggingService.RouteLoggingBinder;
import com.gen.kanazawasansaku.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.j256.ormlite.dao.Dao;

public class MapActivity extends Activity implements OnLocationChangedListener, ServiceConnection {
	
	// 自分の軌跡を描画するときのラインのパラメータ
	private static final int LINE_COLOR = Color.BLUE;
	private static final int LINE_WIDTH = 3;
	
	private GoogleMap googleMap;
	private ImageButton btnStartLog, btnEndLog;

	private boolean isLogging 	 = false;
	private List<LatLng> latlngs = new LinkedList<LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// 記録開始ボタンから遷移してきていればすぐに記録を開始する
		Intent params = getIntent();
		isLogging = params.getBooleanExtra("isLogging", false);
		
		btnStartLog = (ImageButton) findViewById(R.id.imageStartLog);
		btnStartLog.setOnClickListener(onStartLogButtonClick);
		btnStartLog.setVisibility(
				(isLogging) ? View.INVISIBLE : View.VISIBLE);
		
		btnEndLog = (ImageButton) findViewById(R.id.imageEndLog);
		btnEndLog.setOnClickListener(onEndLogButtonClick);
		btnEndLog.setVisibility(
				(isLogging) ? View.VISIBLE : View.INVISIBLE);
		
		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		

		// 位置情報をバックグラウンドで取得する。
		bindService(new Intent(this, RouteLoggingService.class), this, Context.BIND_AUTO_CREATE);
		
		// ルートが選択されていればルートの一番最初の座標にカメラを動かす。
		Route selectRoute = (Route) params.getSerializableExtra("selectRoute");
		if (selectRoute != null) {
			List<LatLng> latlngs = Utils.toList(selectRoute.getRouteJson());
			drawPolyline(latlngs);
			moveToCamera(latlngs.get(0), 17, true);
		}
		
		// 航空写真で表示する
//		googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// 記録中でなければサービスを終了
		if (!isLogging) {}
			unbindService(this);
	}
	
	private OnClickListener onStartLogButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isLogging = true;
			// 開始ボタンと終了ボタンを入れ替える
			btnStartLog.setVisibility(View.INVISIBLE);
			btnEndLog.setVisibility(View.VISIBLE);
		}
		
	};
	
	private OnClickListener onEndLogButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isLogging = false;
			drawPolyline(latlngs);
			
			RegistRouteDialog dialog = new RegistRouteDialog();
			dialog.setOnRegistRouteListener(onRegistRouteListener);
			dialog.show(getFragmentManager(), RegistRouteDialog.TAG);
			
			// 開始ボタンと終了ボタンを入れ替える
			btnStartLog.setVisibility(View.VISIBLE);
			btnEndLog.setVisibility(View.INVISIBLE);
		}
	};
	
	private final OnRegistRouteListener onRegistRouteListener = new OnRegistRouteListener() {
		
		@Override
		public void onRegist(View dialogView) {
			EditText editTitle 		 = (EditText) dialogView.findViewById(R.id.editRouteTitle); 
			EditText editDescription = (EditText) dialogView.findViewById(R.id.editRouteDescription); 
			
			String title 	   = editTitle.getText().toString();
			String description = editDescription.getText().toString();
			
			Route route = new Route.Builder(title, Utils.toJsonStr(latlngs))
				.description(description)
				.timeRequired(Utils.calcRouteTimeRequire(latlngs))
				.build();
			
			try {
				Dao<Route, Integer> dao = DbAccess.getDaoInstance(Route.class);
				// createの戻り値はその追加したオブジェクトのIDではないことに注意
				dao.create(route);
				// 直前に追加されたオブジェクトのIDを取得して設定する。
				route.setId(dao.extractId(route));
				
			} catch (SQLException e) {
				e.printStackTrace();
			}

			latlngs = new ArrayList<LatLng>();
		}
		
		@Override
		public void onCancel() {
			latlngs = new ArrayList<LatLng>();
		};
		
	};
	
	@SuppressWarnings("unused")
	private Route getRouteById (int id) {
		
		Dao<Route, Integer> dao = DbAccess.getDaoInstance(Route.class);
		try {
			return dao.queryForId(Integer.valueOf(id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void drawPolyline (List<LatLng> latlngs) {
		PolylineOptions options = new PolylineOptions();
		options.color(LINE_COLOR);
		options.width(LINE_WIDTH);
		
		for (LatLng latlng : latlngs) {
			options.add(latlng);
		}
		
		googleMap.addPolyline(options);
	}
	
	@Override
	public void onLocationChangedListener(LatLng nowLatLng) {
		Utils.Log.d("onLocationChanged");
		
		//Toast.makeText(this, "latlng : " + nowLatLng, Toast.LENGTH_SHORT).show();
		
		//  記録中のみ現在地にカメラを動かす。
		if (isLogging) {
			latlngs.add(nowLatLng);
			moveToCamera(nowLatLng, 18, true);
		}
		
	}
	
	private void moveToCamera (LatLng latlng, int zoom, boolean isAnimation) {
		CameraPosition cameraPosition = new CameraPosition(
				/* position = */ latlng,
				/* zoom		= */ zoom,
				/* tilt		= */ 10,
				/* bearring = */ 0);
		
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
		if (isAnimation) googleMap.animateCamera(update);
		else 			 googleMap.moveCamera(update);
		
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		RouteLoggingBinder binder = (RouteLoggingBinder) service;
		RouteLoggingService routeLoggingService = binder.getService();
		routeLoggingService.setOnLocationChangedListener(this);
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {}
	
}
