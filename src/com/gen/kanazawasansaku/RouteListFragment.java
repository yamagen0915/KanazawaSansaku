package com.gen.kanazawasansaku;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gen.kanazawasansaku.DbAccess.Route;
import com.gen.kanazawasansaku.utils.FileUtils;
import com.gen.kanazawasansaku.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.Dao;

public class RouteListFragment extends ListFragment {
	
	private OnSelectRouteListener onSelectRouteListener;
	
	private static final Route[] SAMPLE_ROUTE = new Route[] {
		new Route.Builder("AED", "").description("町中のどこにAEDがあるかを確かめながら散歩ができるコースです。日頃からAEDの場所を知っておくことは大事です。").iconId(2).timeRequired(120).build(),
		new Route.Builder("公園", "").description("金沢にある公園を巡るコースです。公園に立ち寄って休憩していくのもいいかもしれないですね。").iconId(0).timeRequired(120).build(),
		new Route.Builder("文化・歴史 その１", "").description("金沢城周辺を回るコースです。").iconId(3).timeRequired(120).build(),
		new Route.Builder("文化・歴史 その２", "").description("有名な東茶屋街を通る散歩コースです。").iconId(4).timeRequired(120).build(),
		new Route.Builder("文化・歴史 その3", "").description("芸術村などを通過するコースです。あなたもアートの世界に飛び込んでみませんか？").iconId(5).timeRequired(120).build(),
	};
	
	private static final String[] SAMPLE_ROUTE_FILES = new String[]{
		"aed",
		"park",
		"bunka_1",
		"bunka_2",
		"bunka_3",
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// リストの区切り線の色を設定する
		getListView().setDivider(getResources().getDrawable(R.color.route_list_bg));
		
		Bundle params 	 = getArguments();
		String routeType = params.getString("routeType");
		
		if (routeType.equals("MyRoute")) {
			setListAdapter(new RouteListAdapter(
					/* context = */ getActivity(), 
					/* routes  = */ getRouteByType(routeType)));
		} else {
			setListAdapter(new RouteListAdapter(
					/* context = */ getActivity(),
					/* routes  = */ getRecomendRoute(getActivity())));
		}
		
	}
	
	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		Route route = (Route) list.getAdapter().getItem(position);
		if (onSelectRouteListener != null) onSelectRouteListener.onSelected(route);
	}
	
	private List<Route> getRouteByType (String type) {
		Dao<Route, Integer> dao = DbAccess.getDaoInstance(Route.class);
		try {
			
			List<Route> routes = dao.queryForEq("routeType_id", type);
			if (routes.size() > 0) 
				Utils.Log.d("route type", ""+routes.get(0).getRouteType());
			return routes;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<DbAccess.Route>();
	}
	
	private static List<Route> getRecomendRoute (Context context) {
		
		List<Route> routes = new ArrayList<Route>();
		for (int j=0; j<SAMPLE_ROUTE_FILES.length; j++) {
			String filename = SAMPLE_ROUTE_FILES[j];
			Route route = SAMPLE_ROUTE[j];
			JSONObject json = FileUtils.loadFile(filename, context);
			try {
				JSONObject jsonRoutes = json.getJSONObject("routes");
				JSONObject jsonRoute  = jsonRoutes.getJSONObject("route");
				JSONArray jsonP 	  = jsonRoute.getJSONArray("p");
				List<LatLng> latlngs = new ArrayList<LatLng>();
				for (int i=0; i<jsonP.length(); i++) {
					JSONObject jsonLatLng = jsonP.getJSONObject(i);
					double lat = Double.parseDouble(jsonLatLng.getString("-lat"));
					double lng = Double.parseDouble(jsonLatLng.getString("-lon"));
					latlngs.add(new LatLng(lat, lng));
				}
				
				String routeJson = Utils.toJsonStr(latlngs);
				route.setRouteJson(routeJson);
				route.setTimeRequired(Utils.calcRouteTimeRequire(latlngs));
				
				Utils.Log.d("timeRequired", route.getTimerequired()+"分");
				
				routes.add(route);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return routes;
	}
	
	
	public void setOnSelectRouteListener (OnSelectRouteListener listener) {
		this.onSelectRouteListener = listener;
	}
	
	public static interface OnSelectRouteListener {
		public void onSelected (Route route); 
	}
	
	public static class RouteListAdapter extends ArrayAdapter<Route> {
		
		private Context context;
		private LayoutInflater inflater;
		
		public RouteListAdapter(Context context) {
			this(context, null);
		}
		
		public RouteListAdapter(Context context, List<Route> list) {
			super(context, 0, 0, list);
			this.context = context;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView == null) 
				convertView = inflater.inflate(R.layout.layout_route_list, null);
			
			Route item = getItem(position);
			
			TextView textRouteTitle 	  = (TextView) convertView.findViewById(R.id.textRouteTitle);
			TextView textRouteDescription = (TextView) convertView.findViewById(R.id.textRouteDescription);
			TextView textTimeRequired 	  = (TextView) convertView.findViewById(R.id.textTimeRequired);
			
			textRouteTitle.setText(item.getTitle());
			textRouteDescription.setText(item.getDescription());
			textTimeRequired.setText(item.getTimerequired() + "分");
			
			ImageView imageIcon = (ImageView) convertView.findViewById(R.id.imageIcon);
			int id    = item.getIconId();
			int resId = context.getResources().getIdentifier("icon"+id, "drawable", context.getPackageName());
			imageIcon.setImageResource(resId);
			
			return convertView;
		}
		
	}
}
