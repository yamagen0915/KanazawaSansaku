package com.gen.kanazawasansaku;

import java.io.Serializable;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gen.kanazawasansaku.utils.Utils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

public class DbAccess extends OrmLiteSqliteOpenHelper {
	
    private static final String DATABASE_NAME = "kanazawasansaku.db";
    private static final int DATABASE_VERSION = 1;

    private static DbAccess dbAccess;

    private DbAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static void initInstance(Context context) {
        dbAccess = new DbAccess(context);
    }

    /**
     * getDaoというメソッド名ではオーバーライドすることになるのでエラーがでます。
     *
     * @param cls
     * @return
     */
	public static <T> Dao<T, Integer> getDaoInstance(Class<T> cls) {
        try {
            return dbAccess.getDao(cls);
        } catch (SQLException e) {
            Utils.Log.d("getDaoInstance in DbAccess", e.toString());
        }

        return null;
    }
    
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
            TableUtils.createTable(getConnectionSource(), Route.class);
            TableUtils.createTable(getConnectionSource(), RouteType.class);
            
            // RouteTypeの初期データを追加
            Dao<RouteType, Integer> dao = getDao(RouteType.class);
            dao.create(new RouteType("MyRoute"));
            dao.create(new RouteType("RecomentRoute"));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2, int arg3) {
	}
	
    @DatabaseTable(tableName="routes")
    public static class Route implements Serializable {
    	
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unused")
		private static final String TABLE_NAME = "routes";
    	
    	@DatabaseField(generatedId = true)
    	private Integer id;
    	
    	@DatabaseField(defaultValue="") 
    	private String title;
    	
    	@DatabaseField(defaultValue="")
    	private String description;
    	
    	@DatabaseField(defaultValue="0")
    	private Integer iconId;
    	
    	@DatabaseField(defaultValue="0")
    	private Integer timeRequired;
    	
    	@DatabaseField(defaultValue="")
    	private String routeJson;
    	
    	 @DatabaseField(foreign = true, foreignAutoRefresh = true)
         private RouteType routeType;

     	// 引数なしのコンストラクタは必須。
    	public Route () {}
    	
    	public Route (Builder builder) {
			this.title 		  = builder.title;
			this.description  = builder.description;
			this.iconId 	  = builder.iconId;
			this.timeRequired = builder.timeRequired;
			this.routeJson 	  = builder.routeJson;
			this.routeType 	  = builder.routeType;
		}
    	
		public Route(String title, String description, Integer iconId, Integer timeRequired, String routeJson, RouteType routeType) {
			this.title 		  = title;
			this.description  = description;
			this.iconId		  = iconId;
			this.timeRequired = timeRequired;
			this.routeJson 	  = routeJson;
			this.routeType 	  = routeType;
		}

		public Integer getId() 			 { return id; }
		public String getTitle() 		 { return title; }
		public String getDescription() 	 { return description; }
		public Integer getIconId()		 { return iconId; }
		public String getRouteJson() 	 { return routeJson; }
		public Integer getTimerequired() { return timeRequired; }
		public RouteType getRouteType()  { return routeType; }
		
		public void setId(Integer id) 					  { this.id = id; }
		public void setTitle(String title) 				  { this.title = title; }
		public void setDescription(String description) 	  { this.description = description; }
		public void setIconId(Integer iconId) 			  { this.iconId = iconId; }
		public void setRouteJson(String routeJson) 		  { this.routeJson = routeJson; }
		public void setTimeRequired(Integer timeRequired) { this.timeRequired = timeRequired; }
		public void setRouteType(RouteType routeType)     { this.routeType = routeType; }
    	
		public static class Builder {
			
			// 必須パラメータ
			private String title;
			private String routeJson;
			
			// オプションパラメータ
			private String description 	 = "";
			private Integer timeRequired = 0;
			private Integer iconId 	   	 = 0;
			private RouteType routeType  = new RouteType("MyRoute");
			
			public Builder (String title, String routeJson) {
				this.title 	   	  = title;
				this.routeJson 	  = routeJson;
			}
			
			public Builder description (String description) 
				{ this.description = description; return this; }
			
			public Builder timeRequired (Integer timeRequired) 
				{ this.timeRequired = timeRequired; return this; }
			
			public Builder iconId (Integer iconId) 
				{ this.iconId = iconId; return this; }
			
			public Builder routeType (RouteType routeType) 
				{ this.routeType = routeType; return this; }
			
			public Route build () 
				{ return new Route(this); }
		}
 		
    }
    
    @DatabaseTable(tableName="route_types")
    public static class RouteType implements Serializable {
    	
		private static final long serialVersionUID = 1L;

		@DatabaseField(id=true, defaultValue="")
    	private String type;
    	
    	@ForeignCollectionField
        private ForeignCollection<Route> route;
    	
    	public RouteType () {}
    	
    	public RouteType (String type) {
    		this.type = type;
    	}
    	
    	public String getType () { return this.type; }
    	
    	@Override
    	public String toString() {
    		return this.type;
    	}
    }

}
