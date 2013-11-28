package com.gen.kanazawasansaku.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Utils {
	
	private Utils () {}
	
	public static final String TAG 		= "kanazawasansaku";
	public static final boolean isDebug = true;
	
	public static void moveToCamera (GoogleMap googleMap, LatLng latlng, int zoom, boolean isAnimation) {
		CameraPosition cameraPosition = new CameraPosition(
				/* position = */ latlng,
				/* zoom		= */ zoom,
				/* tilt		= */ 10,
				/* bearring = */ 0);
		
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
		if (isAnimation) googleMap.animateCamera(update);
		else 			 googleMap.moveCamera(update);
	}
	
	public static String toJsonStr (List<LatLng> latlngs) {
		
		JSONArray jsonLatlngs = new JSONArray();
		
		for (LatLng latlng : latlngs) {
			JSONObject jsonLatlng = new JSONObject();
			try {
				jsonLatlng.put("lat", latlng.latitude);
				jsonLatlng.put("lng", latlng.longitude);
				jsonLatlngs.put(jsonLatlng);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return jsonLatlngs.toString();
		
	}
	
	public static List<LatLng> toList (String json) {
		
		try {
			JSONArray jsonLatLngs = new JSONArray(json);
			
			List<LatLng> latlngs = new ArrayList<LatLng>();
			
			for (int i=0; i<jsonLatLngs.length(); i++) {
				JSONObject jsonLatLng = jsonLatLngs.getJSONObject(i);
				LatLng latlng = new LatLng(
						Double.parseDouble(jsonLatLng.getString("lat")), 
						Double.parseDouble(jsonLatLng.getString("lng")));
				latlngs.add(latlng);
			}
			
			return latlngs;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<LatLng>();
		
	}
	
	public static int calcRouteTimeRequire(List<LatLng> latlngs) {

		final double walkSpeedMM = 4 * 1000 / 60;

		int timeRequire = 0;
		LatLng preLatLng = null;
		for (LatLng latlng : latlngs) {
			if (preLatLng == null) {
				preLatLng = latlng;
				continue;
			}

			double distanceM = Coords.mesureDistanceMeter(preLatLng, latlng);
			timeRequire += (int) (distanceM / walkSpeedMM);
			preLatLng = latlng;
		}

		return timeRequire;
	}
	
	 /**
     * 指定された横幅に合わせて画像をリサイズする
     * 縦横比は固定
     * @param width
     * @param src
     * @return
     */
    public static Bitmap resizeBitmapFitWidth(float width, Bitmap src) {
    	float scale = width / (float)src.getWidth();
    	return scaledBitmap(scale, src);
    } 
    
    /**
     * 指定された高さに合わせて画像をリサイズする
     * 縦横比は固定
     * @param height
     * @param src
     * @return
     */
    public static Bitmap resizeBitmapFitHeight(float height, Bitmap src) {
    	float scale = height / (float)src.getHeight();
    	return scaledBitmap(scale, src);
    }
    
    /**
     * 指定された倍率に合わせて画像を拡大縮小する
     * 縦横比は固定
     * @param scale
     * @param src
     * @return
     */
    private static Bitmap scaledBitmap(float scale, Bitmap src) {
    	if (scale == 0) 					 return null;
    	if (src == null || src.isRecycled()) return null;
    	
    	// リサイズしたBitmapオブジェクトを新しく生成し、古いオブジェクトは解放する。
    	int dstWidth = (int)(src.getWidth() * scale);
    	int dstHeight = (int)(src.getHeight() * scale);
    	Bitmap dstBmp = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
    	
    	// 画像オブジェクトの開放
    	src.recycle();
    	src = null;
    	
    	return dstBmp;
    	
    }
	
	public static class Log {
    	private Log(){}

        public static void e(String msg){
            Utils.Log.e("", msg);
        }
        public static void e(String tag, String msg) {
            if (isDebug) android.util.Log.e(TAG + "@" + tag, msg);
        }

        public static void d(String msg) {
        	Utils.Log.d("", msg);
        }
        public static void d(String tag, String msg) {
            if (isDebug)  android.util.Log.d(TAG + "@" + tag, msg);
        }
    }

}
