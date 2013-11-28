package com.gen.kanazawasansaku.apis;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.gen.kanazawasansaku.utils.Utils;

public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
	
	private OnImageDownloadListener listener;

	@Override
	protected Bitmap doInBackground(String... params) {
		
		if (params.length <= 0) return null;
		
		String url = params[0];
		return fetchImageFromUrl(url);
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (listener != null) listener.onDownload(result);
	}
	
	public ImageDownloadTask setOnImageDownloadListener (OnImageDownloadListener listener) {
		this.listener = listener;
		return this;
	}
	
	private static Bitmap fetchImageFromUrl(String imageUrl) {
        byte[] result = getByteArrayFromUrl(imageUrl);
        Bitmap image = null;
        if (result != null && result.length > 0) {
            image = BitmapFactory.decodeByteArray(result, 0, result.length);
        }

        return image;
    }

    private static byte[] getByteArrayFromUrl(String imageUrl) {

        byte[] result 				 = null;
        HttpURLConnection connection = null;
        InputStream in 				 = null;

        try {
            URL url = new URL(imageUrl);
            
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            in = connection.getInputStream();
            result = toByteArray(in);

        } catch (FileNotFoundException e) {
            Utils.Log.e(e.toString());
            return null;
        } catch (IOException e) {
			e.printStackTrace();
		}  finally {
        	// リソースの開放
            if(connection != null)
                connection.disconnect();
            try {
                if(in != null) in.close();
            } catch (IOException e2) {
            	Utils.Log.e(e2.toString());
            }
        }

        return result;
    }

    private static byte[] toByteArray(InputStream in) {

        int size	  = 0;
        byte[] result = null;
        byte[] line   = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while (true) {
                size = in.read(line);
                if (size <= 0) break;
                out.write(line, 0, size);
            }

            result = out.toByteArray();
        } catch (IOException e) {

        } finally {
            try {
                if(out != null) out.close();
            } catch (IOException e1) {}
        }

        return result;
     }
    
	public static interface OnImageDownloadListener {
		public void onDownload(Bitmap bitmap);
	}

}
