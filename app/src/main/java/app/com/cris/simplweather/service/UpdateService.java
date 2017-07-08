package app.com.cris.simplweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.List;

import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.net.DownLoadManager;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.DiskCacheManager;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.utils.PreferenceUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by Cris on 2017/7/6.
 */

public class UpdateService extends Service {

    private List<String> mCityIds;
    private final static int HOUR = 1000*60*60;
    private int updateFrequency;

    private PendingIntent mPendingIntent;
    private  AlarmManager   manager;

    private Gson mGson = new Gson();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        updateFrequency = intent.getIntExtra(Constants.INTENT_KEY_UPDATE_FRE, 2);

        LogUtil.d(Constants.DEBUG_TAG,"update service started, fre is : " + updateFrequency);

            setUpdateFrequency(updateFrequency);

            if(PreferenceUtil.getboolean(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.IS_DB_CREATED,false)){
                mCityIds = CityListDatabase.getInstance(getApplicationContext()).loadAllChosenCityId();

                for (String id: mCityIds){
                    LogUtil.d(Constants.DEBUG_TAG,"update service get city id: "+ id  );
                    loadWeatherDataFromServer(id);
                }
            }

        return super.onStartCommand(intent, flags, startId);
    }

    private void setUpdateFrequency(int fre){

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long  updatePeriod= fre*HOUR;
        long triggerTime = updatePeriod + SystemClock.elapsedRealtime();
        Intent i = new Intent(this, WakeUpReciever.class);
        mPendingIntent= PendingIntent.getBroadcast(this, 0 ,i, FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime,mPendingIntent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            if (null != manager && null != mPendingIntent) {
                manager.cancel(mPendingIntent);
            }

    }

    private void  loadWeatherDataFromServer(final String cityId){

                DownLoadManager.getInstance().getWeatherResponse(cityId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<WeatherEntity>() {
                                       @Override
                                       public void accept(@NonNull WeatherEntity weatherEntity) throws Exception {
                                           if(null != weatherEntity){
                                               String weatherEntityStr = mGson.toJson(weatherEntity);
                                               DiskCacheManager.getInstance(getApplicationContext()).putString(cityId, weatherEntityStr);
                                               PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.REFRESH_TIME, System.currentTimeMillis());
                                               LogUtil.d(Constants.DEBUG_TAG,"service finished retrieving weather data from server  "+ weatherEntity.toString()  );

                                           }
                                       }
                                   },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {

                                        LogUtil.d(Constants.DEBUG_TAG, "Get weather info from server fail: city id: " + cityId +" " + throwable.getMessage().toString());

                                    }
                                });

    }

}
