package com.gen.kanazawasansaku.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class FileUtils {
	
	private FileUtils () {}
	
    /**
     * ÉtÉ@ÉCÉãÇ©ÇÁJSONObjectÇì«Ç›çûÇﬁ
     * @param filename
     * @param context
     * @return
     */
    public static JSONObject loadFile(String filename, Context context) {
        int resId = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
        InputStream in = context.getResources().openRawResource(resId);

        try {
            return toJSONObject(in);
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.Log.e("loadFile in Common", e.toString());
        }

        return null;
    }
    
    /**
     * 
     * @param inputStream
     * @return
     * @throws JSONException
     */
    private static JSONObject toJSONObject(InputStream inputStream) throws JSONException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jsonStr = new StringBuilder();

        try {
            String str = null;
            while ((str = reader.readLine()) != null) {
                jsonStr.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONObject(jsonStr.toString());
    }

}
