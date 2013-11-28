
package com.gen.kanazawasansaku;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gen.kanazawasansaku.GpsService.GpsBinder;
import com.gen.kanazawasansaku.GpsService.OnLocationChangedListener;
import com.gen.kanazawasansaku.apis.AddSpotTask;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Spot;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Tag;
import com.gen.kanazawasansaku.apis.PostImageTask;
import com.gen.kanazawasansaku.interfaces.OnApiResultListener;
import com.gen.kanazawasansaku.utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class AddSpotActivity extends FragmentActivity implements OnLocationChangedListener, ServiceConnection {
	
	private static final int REQUEST_CAMERA = 100;
	
	private GoogleMap googleMap;
	private ArrayList<EditText> editTagViews = new ArrayList<EditText>();
	
	private Bitmap takenPicture = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_spot);
		
		// 現在地を取得する。
		bindService(new Intent(this, GpsService.class), this, Context.BIND_AUTO_CREATE);
		
		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		
		LatLng latLng = new LatLng(
				googleMap.getMyLocation().getLatitude(), 
				googleMap.getMyLocation().getLongitude());
		Utils.moveToCamera(googleMap, latLng, 18, false);
		
		addTagView();
		
		Button btnAddTag = (Button) findViewById(R.id.btnAddSpotAddTag);
		btnAddTag.setOnClickListener(onAddTagClick);
		
		TextView textAddPicture = (TextView) findViewById(R.id.textAddSpotAddPicture);
		textAddPicture.setOnClickListener(onAddPictureClick);
		
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
		Utils.moveToCamera(googleMap, latlng, 18, false);
	}
	
	private final OnClickListener onAddTagClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			addTagView();
		}
	};
	
	private final OnClickListener onAddPictureClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			startActivityForResult(intent, REQUEST_CAMERA);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode != REQUEST_CAMERA) return ;
		if (resultCode != RESULT_OK) return ;
		
		// 「写真を追加」というテキストを非表示にする。
		TextView textAddPicture = (TextView) findViewById(R.id.textAddSpotAddPicture);
		textAddPicture.setOnClickListener(null);
		textAddPicture.setVisibility(View.INVISIBLE);
		
		Bundle bundle  = data.getExtras();
		takenPicture = (Bitmap) bundle.getParcelable("data");
		
		ImageView imagePicture = (ImageView) findViewById(R.id.imageAddSpotPicture);
		imagePicture.setImageBitmap(takenPicture);
		
	};
	
	private void saveSpot () {
		Location myLocation = googleMap.getMyLocation();
		LatLng myLatLng 	= new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		
		final Spot spot = new Spot(
			/* id	 		= */ null,
			/* title 		= */ ((EditText) findViewById(R.id.editAddSpotTitle)).getText().toString(),
			/* description  = */ ((EditText) findViewById(R.id.editAddSpotDescription)).getText().toString(),
			/* latLng 		= */ myLatLng,
			/* tags 		= */ getTags(editTagViews),
			/* picture 		= */ takenPicture);
		
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle("アップロード中");
		dialog.setMessage("少々お待ちください");
		dialog.setCancelable(false);
		dialog.show();
		
		new AddSpotTask()
			.setOnApiResultListener(new OnApiResultListener() {
				
				@Override
				public void onResult(String result) {
					Utils.Log.d("result", result);
					
					spot.setId(getSpotId(result));
					
					new PostImageTask()
						.setOnApiResultListener(new OnApiResultListener() {
							
							@Override
							public void onResult(String result) {
								Utils.Log.d("result", result);
								dialog.dismiss();
								unbindService(AddSpotActivity.this);
								finish();
							}
						})
						.execute(spot);
				}
			})
			.execute(spot);
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
	
	private static int getSpotId (String jsonStr) {
		try {
			JSONObject json = new JSONObject(jsonStr);
			JSONArray jsonSpotIds = json.getJSONArray("spot_ids");
			return jsonSpotIds.getInt(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return -1;
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
