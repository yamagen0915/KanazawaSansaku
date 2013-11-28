package com.gen.kanazawasansaku;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gen.kanazawasansaku.apis.ImageDownloadTask;
import com.gen.kanazawasansaku.apis.ImageDownloadTask.OnImageDownloadListener;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Spot;
import com.gen.kanazawasansaku.utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity {
	
	public static final String PARAM_SPOT = "spot";
	
	private GoogleMap googleMap;
	
	private Spot spot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
		
		// 記録開始ボタンから遷移してきていればすぐに記録を開始する
		Intent params 	= getIntent();
		spot = (Spot) params.getSerializableExtra("spot");
		
		final ProgressDialog dialog = makeProgressDialog();
		dialog.show();
		
		new ImageDownloadTask()
			.setOnImageDownloadListener(new OnImageDownloadListener() {
				
				@Override
				public void onDownload(Bitmap bitmap) {
					spot.setPicture(bitmap);
					addMarker(spot.getLatLng(), googleMap);
					dialog.dismiss();
				}
			})
			.execute(KanazawaSansakuAPI.getImagePath(spot.getId()));
		
		// スポットの位置にカメラを動かす。
		Utils.moveToCamera(googleMap, spot.getLatLng(), 18, false);
	}
	
	private static void addMarker (LatLng latlng, GoogleMap map) {
		MarkerOptions options = new MarkerOptions();
		options.position(latlng);
		map.addMarker(options);
	}
	
	@SuppressWarnings("unused")
	private static void addMarker (String title,Bitmap icon, LatLng latlng, GoogleMap map) {
		MarkerOptions options = new MarkerOptions();
		options.position(latlng);
		options.title(title);
		if (icon != null)
			options.icon(BitmapDescriptorFactory.fromBitmap(icon));
		map.addMarker(options);
	}
	
	private ProgressDialog makeProgressDialog () {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle("ダウンロード中");
		dialog.setCancelable(false);
		dialog.setMessage("少々お待ちください");
		dialog.setButton(ProgressDialog.BUTTON_POSITIVE, "キャンセル", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		return dialog;
	}
	
	private class MyInfoWindowAdapter implements InfoWindowAdapter {
		
		private View infoWindow;
		
		public MyInfoWindowAdapter () {
			infoWindow = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.layout_info_window, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			ImageView imagePicture = (ImageView) infoWindow.findViewById(R.id.imageInfoWindowPicture);
			imagePicture.setImageBitmap(spot.getPicture());
			
			TextView textTitle = (TextView) infoWindow.findViewById(R.id.textInfoWindowTitle);
			textTitle.setText(spot.getTitle());
			
			TextView textDescription = (TextView) infoWindow.findViewById(R.id.textInfoWindowDescription);
			textDescription.setText(spot.getDescription());
			
			return infoWindow;
		}
		
	}
	
}
