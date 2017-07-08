package app.com.cris.simplweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.activity.SplashActivity;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.net.DownLoadManager;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.DiskCacheManager;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.utils.PreferenceUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Cris on 2017/7/6.
 */

public class NotificationService extends Service {

    private final static int HOUR = 1000*60*60;
    private String mDefaultCityId;
    private WeatherEntity mWeatherEntity;
    private PendingIntent mPendingIntent;
    private AlarmManager manager;
    private NotificationCompat.Builder mBuilder;

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDefaultCityId = intent.getStringExtra(Constants.INTENT_KEY_CITY_ID);
        if(null == mDefaultCityId){
            mDefaultCityId = PreferenceUtil.getString(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY_ID,"CN101220104");
        }
        LogUtil.d(Constants.DEBUG_TAG, "notification city id is : " + mDefaultCityId);

        setUpdateFrequency(2);
        loadWeatherDataFromServer(mDefaultCityId);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        if(null != manager && null != mPendingIntent)
            manager.cancel(mPendingIntent);
        super.onDestroy();
    }

    private void setUpdateFrequency(long fre){

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long  updatePeriod= fre*HOUR;
        long triggerTime = updatePeriod + SystemClock.elapsedRealtime();

        Intent i = new Intent(this, NotiRefreshReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0 ,i,PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime,mPendingIntent);


    }


    private void showNotification(WeatherEntity weatherEntity){

        Notification notification = null;
        if(null == mBuilder){
            mBuilder=new NotificationCompat.Builder(this);
        }
        mBuilder.setSmallIcon(getCondIconId(weatherEntity.getConCode()));

        RemoteViews  notificationView  = new RemoteViews(getPackageName(), R.layout.notification_small);
        notificationView.setTextColor(R.id.weather_notification_city, Color.BLACK);
        notificationView.setTextColor(R.id.weather_notification_con_text,Color.BLACK);
        notificationView.setTextColor(R.id.weather_notification_tmp,Color.BLACK);
        notificationView.setTextColor(R.id.weather_notification_receive_time,Color.GRAY);
        notificationView.setTextColor(R.id.weather_notification_wind_dir,Color.GRAY);
        notificationView.setTextColor(R.id.weather_notification_wind_lvl,Color.GRAY);

        notificationView.setImageViewResource(R.id.weather_notification_cond_icon,getCondIconId(weatherEntity.getConCode()));
        notificationView.setTextViewText(R.id.weather_notification_city,weatherEntity.getCityName());
        notificationView.setTextViewText(R.id.weather_notification_con_text,weatherEntity.getCurCond());
        notificationView.setTextViewText(R.id.weather_notification_tmp,weatherEntity.getCurTmp() + " ℃");
        notificationView.setTextViewText(R.id.weather_notification_receive_time, "发布时间： " + getUpdateDate(weatherEntity.getUpdateTime()));
        notificationView.setTextViewText(R.id.weather_notification_wind_dir,weatherEntity.getWindDir());
        notificationView.setTextViewText(R.id.weather_notification_wind_lvl,weatherEntity.getWindLvl());



        Intent intent=new Intent(this,SplashActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this  , 0   ,intent, 0);

        notificationView.setOnClickPendingIntent(R.id.weather_notification_main,pendingIntent);
        mBuilder.setContent(notificationView);

        notification = mBuilder.build();
        notification.contentIntent = pendingIntent;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);//显示通知
        startForeground(1, notification);//启动服务

    }

    private void  loadWeatherDataFromServer(final String cityId){

        DownLoadManager.getInstance().getWeatherResponse(cityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherEntity>() {
                               @Override
                               public void accept(@NonNull WeatherEntity weatherEntity) throws Exception {
                                   if(null != weatherEntity){
                                       LogUtil.d(Constants.DEBUG_TAG,"service finished retrieving weather data from server  "+ weatherEntity.toString()  );
                                       showNotification(weatherEntity);
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

    private String getUpdateDate(String date){

        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat returnDf = new SimpleDateFormat("HH:mm");
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnDf.format(d).toString();
    }

    private int getCondIconId(String code){

        Calendar calendar = Calendar.getInstance();
        int hour =  calendar.get(Calendar.HOUR_OF_DAY);
        if(hour > 6 && hour < 18){

            switch (Integer.parseInt(code)){
                case  100:
                    return R.drawable.c_sunny;
                case  101:
                case  102:
                case  103:
                    return R.drawable.c_cloudt;
                case  104:
                    return R.drawable.c_overcast;
                case 201:
                case 202:
                case 203:
                case 204:
                case 205:
                case 206:
                case 207:
                case 208:
                case 209:
                case 210:
                case 211:
                case 212:
                case 213:
                    return R.drawable.c_hurricane;
                case  300:
                case  301:
                    return R.drawable.c_shower_day;
                case  302:
                case  303:
                    return R.drawable.c_thund_shower;
                case  304:
                    return R.drawable.c_hail;
                case  305:
                case  309:
                    return R.drawable.c_rain_little;
                case  306:
                case  307:
                    return R.drawable.c_rain_middle;
                case  308:
                case  312:
                    return R.drawable.c_rain_storm;
                case  310:
                case  311:
                    return R.drawable.c_rain_heavy;
                case 313:
                case 404:
                case 405:
                case 406:
                    return R.drawable.c_snow_rain;
                case  400:
                case  407:
                    return R.drawable.c_snow_little;
                case  401:
                case  402:
                    return R.drawable.c_snow_middle;
                case  403:
                    return R.drawable.c_snow_storm;
                case  500:
                case  501:
                    return R.drawable.c_fog;
                case  502:
                    return R.drawable.c_19;
                case  503:
                case  504:
                    return R.drawable.c_yangchen;
                case  507:
                case  508:
                    return R.drawable.c_sand;
                case  900:
                    return R.drawable.c_sunny;
                case  901:
                    return R.drawable.c_snow_little;
            }
        }else {

            switch (Integer.parseInt(code)) {
                case  100:
                    return R.drawable.d_suny;
                case  101:
                case  102:
                case  103:
                    return R.drawable.d_cloudy;
                case  104:
                    return R.drawable.c_overcast;
                case 201:
                case 202:
                case 203:
                case 204:
                case 205:
                case 206:
                case 207:
                case 208:
                case 209:
                case 210:
                case 211:
                case 212:
                case 213:
                    return R.drawable.c_hurricane;
                case  300:
                case  301:
                    return R.drawable.d_shower;
                case  302:
                case  303:
                    return R.drawable.c_thund_shower;
                case  304:
                    return R.drawable.c_hail;
                case  305:
                case  309:
                    return R.drawable.c_rain_little;
                case  306:
                case  307:
                    return R.drawable.c_rain_middle;
                case  308:
                case  312:
                    return R.drawable.c_rain_storm;
                case  310:
                case  311:
                    return R.drawable.c_rain_heavy;
                case 313:
                case 404:
                case 405:
                case 406:
                    return R.drawable.c_snow_rain;
                case  400:
                case  407:
                    return R.drawable.d_snow_shower;
                case  401:
                case  402:
                    return R.drawable.c_snow_middle;
                case  403:
                    return R.drawable.c_snow_storm;
                case  500:
                case  501:
                    return R.drawable.d_fog;
                case  502:
                    return R.drawable.d_sand;
                case  503:
                case  504:
                    return R.drawable.d_sand;
                case  507:
                case  508:
                    return R.drawable.c_sand;
                case  900:
                    return R.drawable.c_sunny;
                case  901:
                    return R.drawable.c_snow_little;
            }

        }
        return R.mipmap.ic_launcher;
    }

}
