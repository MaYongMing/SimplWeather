package app.com.cris.simplweather.utils;

import android.Manifest;

/**
 * Created by Cris on 2017/6/21.
 */

public class Constants {

    public static final String KEY = "5597b983af944721a3f2dae92b4ccd4d";
    public static final String DEBUG_TAG = "SimpleWeather";
    public static final String CITY_LIST_URL = "https://cdn.heweather.com/china-city-list.txt";
    public static final String INTENT_KEY_CITY_ID = "city_id";
    public static final String INTENT_KEY_STREET_NAME = "street";
    public static final String INTENT_KEY_CITIES_CHANGED = "is_cities_changed";
    public static final String INTENT_KEY_UPDATE_FRE= "update_fre";
    public static final String INTENT_KEY_WEATHER_INFO= "weather_info";

    public static final class Preferences{

        public static final String PREF_NAME = "global_setting";
        public static final String IS_PERMISSION_GRANTED = "is_permission_granted";
        public static final String IS_FIRST_START = "is_first_start";
        public static final String IS_DB_CREATED = "is_database_created";
        public static final String IS_LOCATING_ON = "locating_on";
        public static final String AUTO_UPDATE_FRE = "auto_update";
        public static final String IS_NOTI_SHOW = "show_notification";
        public static final String HAS_DEFAULT_CITY = "has_default_city";
        public static final String DEFAULT_CITY_ID = "default_city";
        public static final String DEFAULT_CITY = "default_city_name";
        public static final String REFRESH_TIME = "refresh_time";


    }

    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

}
