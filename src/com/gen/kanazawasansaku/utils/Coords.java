package com.gen.kanazawasansaku.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * 2‚Â‚ÌÀ•W‚©‚ç‹——£‚ğ‚à‚Æ‚ß‚éB
 * @author yamamoto
 *
 */
public class Coords {

	public static final double GRS80_A = 6378137.000;
	public static final double GRS80_E2 = 0.00669438002301188;
	public static final double GRS80_MNUM = 6335439.32708317;

	public static double mesureDistanceMeter(LatLng latlng1, LatLng latlng2) {
		return calcDistHubeny(latlng1, latlng2, GRS80_A, GRS80_E2, GRS80_MNUM);
	}

	private static double calcDistHubeny(LatLng latlng1, LatLng latlng2, double a, double e2, double mnum) {
		double my = deg2rad((latlng1.latitude + latlng2.latitude) / 2.0);
		double dy = deg2rad(latlng1.latitude - latlng2.latitude);
		double dx = deg2rad(latlng1.longitude - latlng2.longitude);

		double sin = Math.sin(my);
		double w = Math.sqrt(1.0 - e2 * sin * sin);
		double m = mnum / (w * w * w);
		double n = a / w;

		double dym = dy * m;
		double dxncos = dx * n * Math.cos(my);

		return Math.sqrt(dym * dym + dxncos * dxncos);
	}

	private static double deg2rad(double deg) {
		return deg * Math.PI / 180.0;
	}

}
