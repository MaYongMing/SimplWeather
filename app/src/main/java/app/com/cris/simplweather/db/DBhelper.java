package app.com.cris.simplweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper  extends SQLiteOpenHelper{

	private static final String CREATE_CITY_LIST = "create table city_list(id integer primary key autoincrement, district_name text, eng_name text, city_name text, province_name text,city_id text)";
	private static final String CREATE_CITY_CHOSEN = "create table city_chosen(id integer primary key autoincrement, city_id text, city_name text, UNIQUE(city_id))";

	public DBhelper(Context context, String name, CursorFactory factory,
			int version) {

		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITY_LIST);
		db.execSQL(CREATE_CITY_CHOSEN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
