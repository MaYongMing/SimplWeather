package app.com.cris.simplweather.db;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import app.com.cris.simplweather.model.CityEntity;

public class CityListDatabase {

	private static final String DB_NAME="SimpleWeather";
	private static final int DB_VERSION=1;
	private SQLiteDatabase db;
	private static CityListDatabase weatherDB;

	private CityListDatabase(Context context){
		DBhelper dBhelper =  new DBhelper(context, DB_NAME, null, DB_VERSION);

		db = dBhelper.getWritableDatabase();
	}
	public static synchronized CityListDatabase getInstance(Context context){
		if (weatherDB==null) weatherDB=new CityListDatabase(context);
		return weatherDB;
	}

	public void beginTransaction(){
		db.beginTransaction();
	}
	public void setTransactionSuccessful(){
		db.setTransactionSuccessful();
	}
	public void endTransaction(){
		db.endTransaction();
	}
	public void saveDistrict(CityEntity cityEntity){
		if (cityEntity!=null){

			db.execSQL("Insert Into city_list(district_name, eng_name, city_name, province_name, city_id) values(?,?,?,?,?)", new String[]{cityEntity.getDistrictName(),cityEntity.getDistrictEngName(),cityEntity.getCityName(),cityEntity.getProvinceName(),cityEntity.getCityId()});
		}
	}

	public List<CityEntity> loadDistrict(String districtName){

		List<CityEntity> disList=new ArrayList<>();
		Cursor cursor = db.query("city_list", null, "district_name = ?", new String[]{districtName}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				CityEntity city = new CityEntity();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setDistrictName(cursor.getString(cursor.getColumnIndex("district_name")));
				city.setDistrictEngName(cursor.getString(cursor.getColumnIndex("eng_name")));
				city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
				disList.add(city);
			}while(cursor.moveToNext());
		}if(cursor!=null) cursor.close();
		return disList;
	}

	public List<CityEntity> loadAllData( ){

		List<CityEntity> cityList=new ArrayList<>();
		Cursor cursor = db.rawQuery("select * from city_list",null);
		if(cursor.moveToFirst()){
			do{
				CityEntity city = new CityEntity();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setDistrictName(cursor.getString(cursor.getColumnIndex("district_name")));
				city.setDistrictEngName(cursor.getString(cursor.getColumnIndex("eng_name")));
				city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
				cityList.add(city);
			}while(cursor.moveToNext());
		}if(cursor!=null) cursor.close();
		return cityList;
	}

	
	
}
