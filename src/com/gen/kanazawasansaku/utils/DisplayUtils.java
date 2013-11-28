package com.gen.kanazawasansaku.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtils {
	
	private static Integer displayW, displayH;
	
	private DisplayUtils () {}
	
	public static int mesureDisplayW (Context context) {
		if (displayW != null) return displayW;
		
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display 	   = manager.getDefaultDisplay();
		
		Point displayPoint = new Point();
		display.getSize(displayPoint);
		displayW = displayPoint.x;
		return displayW;
	}
	
	public static int mesureDisplayH (Context context) {
		if (displayH != null) return displayH;
		
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display 	   = manager.getDefaultDisplay();
		
		Point displayPoint = new Point();
		display.getSize(displayPoint);
		displayH = displayPoint.y;
		return displayH;
	}


}
