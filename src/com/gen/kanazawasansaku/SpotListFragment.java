package com.gen.kanazawasansaku;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gen.kanazawasansaku.apis.FetchAllSpotsTask;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Spot;
import com.gen.kanazawasansaku.apis.KanazawaSansakuAPI.Tag;
import com.gen.kanazawasansaku.interfaces.OnApiResultListener;
import com.google.android.gms.maps.model.LatLng;

public class SpotListFragment extends ListFragment {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fetchAllSpot();
	}
	
	public void fetchAllSpot () {
		// サーバーから登録されている全てのスポットを取得し表示する。
		new FetchAllSpotsTask()
			.setOnApiResultListener(new OnApiResultListener() {
				
				@Override
				public void onResult(String result) {
					List<Spot> spots = toSpots(result);
					setListAdapter(new SpotListAdapter(getActivity(), spots));
				}
			})
			.execute();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// クリックされたスポットの情報をつけて画面遷移する。
		SpotListAdapter adapter = (SpotListAdapter) getListAdapter();
		Spot spot = adapter.getItem(position);
		
		Intent intent = new Intent(getActivity(), MapActivity.class);
		intent.putExtra(MapActivity.PARAM_SPOT, spot);
		startActivity(intent);
		
	}
	
	/**
	 * サーバーから送られてきたJSONデータをSpotオブジェクトに変換する。
	 * @param jsonStr
	 * @return
	 */
	private static List<Spot> toSpots(String jsonStr) {
		try {
			JSONObject result = new JSONObject(jsonStr);
			
			if (result.isNull("spots")) return new ArrayList<Spot>();
			
			List<Spot> spots = new ArrayList<KanazawaSansakuAPI.Spot>();
			
			JSONArray jsonSpots = result.getJSONArray("spots");
			for (int i=0; i<jsonSpots.length(); i++) {
				JSONObject jsonSpot = jsonSpots.getJSONObject(i);

				LatLng latlng = new LatLng(
						jsonSpot.getDouble("lat"), 
						jsonSpot.getDouble("lng"));
				
				Spot spot = new Spot(
						/* id 	 		= */ jsonSpot.getInt("id"), 
						/* title 		= */ jsonSpot.getString("title"),
						/* description	= */ jsonSpot.getString("description"), 
						/* latlng 		= */ latlng, 
						/* tags			= */ toTag(jsonSpot.getJSONArray("tags")),
						/* picture		= */ null);
				
				spots.add(spot);
			}
			
			return spots;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<Spot>();
	}
	
	private static List<Tag> toTag(JSONArray jsonTags) {
		
		List<Tag> tags = new ArrayList<KanazawaSansakuAPI.Tag>();
		for (int i=0; i<jsonTags.length(); i++) {
			try {
				JSONObject jsonTag = jsonTags.getJSONObject(i);
				Tag tag = new Tag(null, jsonTag.getString("title"));
				tags.add(tag);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return new ArrayList<Tag>();
	}
	
	private static class SpotListAdapter extends ArrayAdapter<Spot> {
		
		private LayoutInflater inflater;
		
		public SpotListAdapter(Context context, List<Spot> spots) {
			super(context, 0, 0, spots);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) 
				convertView = inflater.inflate(R.layout.layout_sopt_list, null);
			
			Spot spot = getItem(position);
			
			TextView titleView		 = (TextView) convertView.findViewById(R.id.textSpotListTitle);
			TextView descriptionView = (TextView) convertView.findViewById(R.id.textSpotListDescription);
			
			titleView.setText(spot.getTitle());
			descriptionView.setText(spot.getDescription());
			
			return convertView;
		}
		
		
	}
 
}
