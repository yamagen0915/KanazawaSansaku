package com.gen.kanazawasansaku;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gen.kanazawasansaku.GpsService.GpsBinder;
import com.gen.kanazawasansaku.GpsService.OnLocationChangedListener;
import com.gen.kanazawasansaku.apis.AddSpotTask;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Spot;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Tag;
import com.google.android.gms.internal.bt;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class AddSpotActivity extends Activity implements OnLocationChangedListener, ServiceConnection {
	
	private GoogleMap googleMap;
	private ArrayList<EditText> editTagViews = new ArrayList<EditText>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_spot);
		
		// 現在地を取得する。
		bindService(new Intent(this, GpsService.class), this, Context.BIND_AUTO_CREATE);
		
		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		
		addTagView();
		
		Button btnAddTag = (Button) findViewById(R.id.btnAddTag);
		btnAddTag.setOnClickListener(onAddTagClick);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_spot_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.menuSave) {
			saveSpot();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onLocationChangedListener(LatLng latlng) {
		CameraPosition cameraPosition = new CameraPosition(
				/* position = */ latlng,
				/* zoom		= */ 18,
				/* tilt		= */ 10,
				/* bearring = */ 0);
		
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
		googleMap.moveCamera(update);
	}
	
	private final OnClickListener onAddTagClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			addTagView();
		}
	};
	
	private void saveSpot () {
		Location myLocation = googleMap.getMyLocation();
		LatLng myLatLng 	= new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		
		Spot spot = new Spot(
			/* id	 		= */ null,
			/* title 		= */ ((EditText) findViewById(R.id.editAddSpotTitle)).getText().toString(),
			/* description  = */ ((EditText) findViewById(R.id.editAddSpotDescription)).getText().toString(),
			/* latLng 		= */ myLatLng,
			/* tags 		= */ getTags(editTagViews));
		
		new AddSpotTask().execute(spot);
		finish();
	}
	
	private void addTagView () {
		// タグの入力フォームを追加
		EditText editTag = createEditTagView(getApplicationContext());
		LinearLayout linearTags = (LinearLayout) findViewById(R.id.linearAddSpotTags);
		linearTags.addView(editTag);
		editTagViews.add(editTag);
	}
	
	private static List<Tag> getTags(List<EditText> editTagViews) {
		List<Tag> tags = new ArrayList<Tag>();
		for (EditText editTag : editTagViews) {
			Tag tag = new Tag(null, editTag.getText().toString());
			tags.add(tag);
		}
		return tags;
	}
	
	private static EditText createEditTagView (Context context) {
		EditText editTag = new EditText(context);
		editTag.setInputType(InputType.TYPE_CLASS_TEXT);
		editTag.setMaxLines(1);
		editTag.setTextColor(Color.BLACK);
		return editTag;
	} 
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		GpsBinder binder = (GpsBinder) service;
		GpsService gpsService = binder.getService();
		gpsService.setOnLocationChangedListener(this);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {}
	
	

	
}
