package com.gen.kanazawasansaku.apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gen.kanazawasansaku.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

public class KanazawaSansakuAPI {
	
	public static final String URL_ADD_SPOT = "add_spot.php";
	public static final String URL_GET_SPOT = "get_spot.php";
	
	private KanazawaSansakuAPI () {};
	
	
	public static String fetchAllSpots () {
		return sendGetRequest(URL_GET_SPOT);
	}
	
	public static void uploadSpots (Spot[] spots) {
		StringBuilder builder = new StringBuilder();
		builder.append("spots=[");
		for (int i=0; i<spots.length; i++) {
			if (i > 0) builder.append(",");
			String encodedUrl;
			try {
				encodedUrl = URLEncoder.encode(spots[i].toString(), "UTF-8");
				builder.append(encodedUrl);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		builder.append("]");
		
		sendGetRequest(URL_ADD_SPOT + "?" + builder.toString());
	}
	
	private static String sendGetRequest (final String urlWithParam) {
		
		Utils.Log.d("request url", urlWithParam);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpUriRequest request		 = new HttpGet(urlWithParam);
		
		request.setHeader("Content-type", "application/json");
		try {
			HttpResponse response = httpClient.execute(request);
			if (!isHttpOk(response)) return "";
			
			return inputStreamtoString(response.getEntity().getContent());
		} catch (Exception e) {
		}
		
		return "";
	} 
	
	private static boolean isHttpOk (HttpResponse response) {
		return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
	}
	
	private static String inputStreamtoString (InputStream in) {
		if (in == null) return "";
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				buffer.append(str);
			}
			return buffer.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	
	public static class Spot {
		
		private Integer id;
		private String title;
		private String description;
		private LatLng latLng;
		private List<Tag> tags;
		
		public Spot(Integer id, String title, String description, LatLng latLng, List<Tag> tags) {
			this.id = id;
			this.title = title;
			this.description = description;
			this.latLng = latLng;
			this.tags = tags;
		}
		
		public Integer getId() 			{ return id; }
		public String getTitle() 		{ return title; }
		public String getDescription() 	{ return description; }
		public LatLng getLatLng() 		{ return latLng; }
		public List<Tag> getTags() 		{ return tags; }
		
		public void setId(Integer id) 			{ this.id = id; }
		public void setTitle(String title) 		{ this.title = title; }
		public void setDescription(String description) 
												{ this.description = description; }
		public void setLatLng(LatLng latLng)	{ this.latLng = latLng; }
		public void setTags(ArrayList<Tag> tags){ this.tags = tags; }
		
		@Override
		public String toString() {
			try {
				JSONObject spot = new JSONObject();
				spot.put("id", ""+id);
				spot.put("title", title);
				spot.put("description", description);
				spot.put("lat", latLng.latitude);
				spot.put("lng", latLng.longitude);
				
				JSONArray jsonTags = new JSONArray();
				for (Tag tag : tags) {
					JSONObject jsonTag = new JSONObject();
					jsonTag.put("id", tag.getId());
					jsonTag.put("spot_id", ""+id);
					jsonTag.put("title", tag.getTitle());
					jsonTags.put(jsonTag);
				}
				
				spot.put("tags", jsonTags);
				
				return spot.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		}
	}
	
	public static class Tag {
		
		private Integer id;
		private String title;
		
		public Tag(Integer id, String title) {
			this.id = id;
			this.title = title;
		}
		
		public Integer getId() 	 { return id; }
		public String getTitle() { return title; }
		
		public void setId(Integer id) 		{ this.id = id; }
		public void setTitle(String title)  { this.title = title; }
		
	}
}
