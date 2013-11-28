package com.gen.kanazawasansaku.apis;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

import com.gen.kanazawasansaku.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

public class KanazawaSansakuAPI {
	
	public static final String API_URL		  	= "http://www.inct-iken.com/yamagen/kanazawasansaku";
	public static final String URL_ADD_SPOT   	= "add_spot.php";
	public static final String URL_GET_SPOT  	= "get_spot.php";
	public static final String URL_POST_PICTURE = "post_picture.php";
	public static final String URL_PICTURE_DIR  = "up_files";
	
	private KanazawaSansakuAPI () {};
	
	public static String getImagePath(int spotId) {
		return API_URL + "/" + URL_PICTURE_DIR + "/" + "spot" + spotId + ".jpg"; 
	}
	
	public static String postImage (String filename, Bitmap bitmap) {
		HttpPost httpPost = new HttpPost(API_URL + "/" + URL_POST_PICTURE);
		
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("post", toByteArrayBody(filename, bitmap));
		httpPost.setEntity(entity);
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		HttpClient client = new DefaultHttpClient();
		try {
			return client.execute(httpPost, responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String fetchAllSpots () {
		HttpGet request = new HttpGet(API_URL + "/" + URL_GET_SPOT);
		return sendGetRequest(request);
	}
	
	public static String uploadSpots (Spot[] spots) {
		
		JSONArray jsonSpots = new JSONArray();
		for (int i=0; i<spots.length; i++) {
			jsonSpots.put(spots[i].toJSON());
			Utils.Log.d("param", spots[i].toJSON().toString());
		}
		
		JSONObject json = new JSONObject();
		try {
			json.put("spots", jsonSpots);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sendPostRequestWithJSON(API_URL + "/" + URL_ADD_SPOT, json.toString());
	}
	
	private static String sendPostRequestWithJSON (String url, String json) {
		
		Utils.Log.d("request url", url);
		
		HttpPost request = new HttpPost(url);
        try {
            request.setEntity(new StringEntity(json, "UTF-8"));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json; charset=UTF-8");

    		return sendPostRequest(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
	}
	
	public static String sendPostRequest (HttpPost request) {

        HttpClient client = new DefaultHttpClient();
		try {
			String responseStr = client.execute(request, new ResponseHandler<String>() {

                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                }

            });
			
			return (!TextUtils.isEmpty(responseStr)) ? responseStr : "";
		} catch (Exception e) {
		}
		
		return "";
	} 
	
	private static String sendGetRequest (HttpGet request) {
        HttpClient client = new DefaultHttpClient();
		request.setHeader("Content-type", "application/json");
		try {
			String responseStr = client.execute(request, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            });
			
			return (!TextUtils.isEmpty(responseStr)) ? responseStr : "";
		} catch (Exception e) {
		}
		
		return "";
	} 
	
	private static ByteArrayBody toByteArrayBody (String filename, Bitmap bitmap) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, outputStream);
		return new ByteArrayBody(outputStream.toByteArray(), filename);
	}
	
	@SuppressWarnings("unused")
	private static boolean isHttpOk (HttpResponse response) {
		return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
	}
	
	@SuppressWarnings("unused")
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

	
	public static class Spot implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private Integer id;
		private String title;
		private String description;
		private double lat;
		private double lng;
		private List<Tag> tags;
		private Bitmap picture;
		
		public Spot(Integer id, String title, String description, LatLng latLng, List<Tag> tags, Bitmap picture) {
			this.id 		 = id;
			this.title 		 = title;
			this.description = description;
			this.lat 		 = latLng.latitude;
			this.lng 		 = latLng.longitude;
			this.tags		 = tags;
			this.picture	 = picture;
		}
		
		public Integer getId() 			{ return id; }
		public String getTitle() 		{ return title; }
		public String getDescription() 	{ return description; }
		public LatLng getLatLng() 		{ return new LatLng(this.lat, this.lng); }
		public List<Tag> getTags() 		{ return tags; }
		public Bitmap getPicture()		{ return picture; }
		
		public void setId(Integer id) 			{ this.id = id; }
		public void setTitle(String title) 		{ this.title = title; }
		public void setDescription(String description) 
												{ this.description = description; }
		public void setLatLng(LatLng latLng)	{ this.lat = latLng.latitude; this.lng = latLng.longitude; }
		public void setTags(ArrayList<Tag> tags){ this.tags = tags; }
		public void setPicture(Bitmap picture)  { this.picture = picture; }
		
		public JSONObject toJSON() {
			try {
				JSONObject spot = new JSONObject();
				spot.put("id", ""+id);
				spot.put("title", title);
				spot.put("description", description);
				spot.put("lat", lat);
				spot.put("lng", lng);
				
				JSONArray jsonTags = new JSONArray();
				for (Tag tag : tags) {
					JSONObject jsonTag = new JSONObject();
					jsonTag.put("id", tag.getId());
					jsonTag.put("spot_id", ""+id);
					jsonTag.put("title", tag.getTitle());
					jsonTags.put(jsonTag);
				}
				
				spot.put("tags", jsonTags);
				
				return spot;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return new JSONObject();
		}
	}
	
	public static class Tag implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
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
