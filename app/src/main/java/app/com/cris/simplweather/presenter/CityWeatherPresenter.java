package app.com.cris.simplweather.presenter;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.net.DownLoadManager;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.DiskCacheManager;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.utils.NetUtil;
import app.com.cris.simplweather.utils.PreferenceUtil;
import app.com.cris.simplweather.viewinterface.BaseView;
import app.com.cris.simplweather.viewinterface.WeatherView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Cris on 2017/6/25.
 */

public class CityWeatherPresenter extends BasePresenter {

    private boolean isNetworkAvailable;
    private String mTargetCityId;
    private WeatherView mWeatherView;
    private Disposable mDisposable;
    private long mLastRefreshTime;
    private boolean isNeedRefresh;
    private String mWeatherGsonStr;
    private Gson mGson;
    private List<String> mCityIdList;



    private DiskCacheManager mDiskCacheManager;

    @Override
    public void attachView(BaseView baseView) {
        mWeatherView = (WeatherView) baseView;
        mGson = new Gson();
        try {
            mDiskCacheManager = DiskCacheManager.getInstance(mWeatherView.getContext().getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public  void start() {
        mLastRefreshTime = PreferenceUtil.getLong(mWeatherView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME,Constants.Preferences.REFRESH_TIME, System.currentTimeMillis()-60*60*1000);
        isNeedRefresh = (Math.abs(mLastRefreshTime - System.currentTimeMillis()) > 30*60*1000) ? true : false;
        isNetworkAvailable = NetUtil.isNetworkAvailable(mWeatherView.getContext());
        if( null != mCityIdList){
            mCityIdList.clear();
        }
        mCityIdList = CityListDatabase.getInstance(mWeatherView.getContext().getApplicationContext()).loadAllChosenCityId();

        if (isNetworkAvailable ){
            if(null != mWeatherView) {
                mWeatherView.hideRetry();
                mWeatherView.showLoading();
            }
            if(isNeedRefresh){
                LogUtil.d(Constants.DEBUG_TAG,"Need retrieve weather info from server, start loading....");
                loadWeatherDataFromServer(mCityIdList);
            }
            else {
                LogUtil.d(Constants.DEBUG_TAG,"Retrieved weather info from server just now, start reading data from cache...");
                loadWeatherDataFromCache();
            }
        }else {
            if(null != mWeatherView){
                mWeatherView.showRetry(null);
            }

        }

    }

    @Override
    public void stop() {
        if (null != mDisposable && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    public void deliverTargetCity(String cityId){
        mTargetCityId = cityId;
    }

    private void  loadWeatherDataFromServer(List<String> cityIdList){
        if(null != cityIdList)
            for (final String cityId: cityIdList){
                mDisposable = DownLoadManager.getInstance().getWeatherResponse(cityId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<WeatherEntity>() {
                                       @Override
                                       public void accept(@NonNull WeatherEntity weatherEntity) throws Exception {
                                           LogUtil.d(Constants.DEBUG_TAG,"Get weather info from server successfully. Delivering data to View and Writing data to cache...");

                                           refreshContent(weatherEntity);

                                           LogUtil.d(Constants.DEBUG_TAG, weatherEntity.toString());
                                           String weatherEntityStr = mGson.toJson(weatherEntity);
                                           mDiskCacheManager.putString(cityId, weatherEntityStr);

                                           CityListDatabase.getInstance(mWeatherView.getContext().getApplicationContext()).saveCityName(cityId,weatherEntity.getCityName());

                                           if(null != mWeatherView) {
                                               PreferenceUtil.putLong(mWeatherView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.REFRESH_TIME, System.currentTimeMillis());
                                           }
                                       }
                                   },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        //WeatherView.toastMessage("获取天气信息失败，请稍后再试");
                                        LogUtil.d(Constants.DEBUG_TAG, "Get weather info from server fail: city id: " + cityId +" " + throwable.getMessage().toString());
                                        loadFail();
                                    }
                                });
            }

    }

    private void loadWeatherDataFromCache(){

        mWeatherGsonStr = mDiskCacheManager.getString(mTargetCityId);

        if (null == mWeatherGsonStr){
            LogUtil.d(Constants.DEBUG_TAG,"Reading weather data from cache fail, start retrieving data from server...");
            loadWeatherDataFromServer(mCityIdList);
        }else {
            LogUtil.d(Constants.DEBUG_TAG,"Reading weather data from cache successfully, delivering data to view...");

            WeatherEntity weatherEntity = mGson.fromJson(mWeatherGsonStr,WeatherEntity.class);
            LogUtil.d(Constants.DEBUG_TAG,"Get weather info : "+weatherEntity.toString());
            CityListDatabase.getInstance(mWeatherView.getContext().getApplicationContext()).saveCityName(mTargetCityId,weatherEntity.getCityName());
            refreshContent(weatherEntity);
        }
    }

    private void refreshContent(WeatherEntity weatherEntity){
        if(null != mWeatherView){
            mWeatherView.hideLoading();
            mWeatherView.showContent(weatherEntity);
        }
    }
    private void loadFail(){
        if(null != mWeatherView) {
            mWeatherView.hideLoading();

            mWeatherView.showRetry("获取天气信息失败，请稍后再试。");
        }
    }
}
