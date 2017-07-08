package app.com.cris.simplweather.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.security.PrivateKey;
import java.util.List;

import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.model.CityEntity;
import app.com.cris.simplweather.model.LocationEntity;
import app.com.cris.simplweather.net.DownLoadManager;
import app.com.cris.simplweather.service.NotificationService;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.utils.NetUtil;
import app.com.cris.simplweather.utils.PreferenceUtil;
import app.com.cris.simplweather.viewinterface.BaseView;
import app.com.cris.simplweather.viewinterface.SplashView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by Cris on 2017/6/21.
 */

public class SplashPresenter extends  BasePresenter implements AMapLocationListener{

    private SplashView mSplashView;

    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mLocationClient;

    private boolean isFirstLunch;
    private boolean isCityListDBCreated;
    private boolean isNetWorkAvailable;
    private boolean isLocatedSuccess;
    private boolean isLocatingON;

    private LocationEntity mLocatedCity;
    private CityEntity mTargetCity;
    private CityEntity mDefaultCity = new CityEntity();
    private Disposable mDisposable;


    @Override
    public void attachView(BaseView baseView){
        mSplashView = (SplashView) baseView;
    };

    @Override
    public void start(){

        isNetWorkAvailable = NetUtil.isNetworkAvailable(mSplashView.getContext().getApplicationContext());
        isFirstLunch = PreferenceUtil.getboolean(mSplashView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME,Constants.Preferences.IS_FIRST_START,true);
        isCityListDBCreated = PreferenceUtil.getboolean(mSplashView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME,Constants.Preferences.IS_DB_CREATED,false);
        isLocatingON = PreferenceUtil.getboolean(mSplashView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME,Constants.Preferences.IS_LOCATING_ON,true);

        String defaultCityId = PreferenceUtil.getString(mSplashView.getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY_ID,"CN101220104");
        mDefaultCity.setCityId(defaultCityId);

        if(isNetWorkAvailable) {
            if (isFirstLunch || !isCityListDBCreated) {
                PreferenceUtil.putBoolean(mSplashView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.IS_FIRST_START, false);
                downLoadCityList();
            }
            else {
                if(isLocatingON){
                    startLocation();
                }
                else {
                    mSplashView.navigationToCityWeatherActivty(mDefaultCity);
                }
            }
        }
        else {
            LogUtil.d(Constants.DEBUG_TAG,"Please check network state !");
            mSplashView.toastMessage("未检测到网络连接，请查看网络设置");
            mSplashView.navigationToCityWeatherActivty(mDefaultCity);
        }
    }

    @Override
    public void stop() {

        if(null != mLocationClient){

            mLocationClient.onDestroy();
        }
        if(null != mDisposable){

            mDisposable.dispose();

        }
    }

    private void downLoadCityList(){
        LogUtil.d(Constants.DEBUG_TAG,"First launch, start downloading city list...");
        mDisposable = DownLoadManager.getInstance().getCityList()
                .doOnNext(new Consumer<List<CityEntity>>() {
                    @Override
                    public void accept(@NonNull List<CityEntity> cityEntities) throws Exception {
                        if (cityEntities != null){

                            CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).beginTransaction();
                            for (CityEntity city : cityEntities){

                                CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).saveDistrict(city);
                            }
                            CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).setTransactionSuccessful();
                            CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).endTransaction();

                            isCityListDBCreated = true;
                            LogUtil.d(Constants.DEBUG_TAG,"First launch, got city list and wrote into database.");
                            PreferenceUtil.putBoolean(mSplashView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME,Constants.Preferences.IS_DB_CREATED,true);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CityEntity>>() {
                               @Override
                               public void accept(@NonNull List<CityEntity> cityEntities) throws Exception {
                                   startLocation();
                                   LogUtil.d(Constants.DEBUG_TAG,"First launch, downloaded city list, start locating...");
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                isCityListDBCreated = false;
                                Toast.makeText(mSplashView.getContext().getApplicationContext(),"获取城市列表失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                PreferenceUtil.putBoolean(mSplashView.getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.IS_DB_CREATED,false);
                                LogUtil.d(Constants.DEBUG_TAG,"Download City list failed: "+throwable.getMessage().toString());
                            }
                        });
    }

    private void startLocation(){

        LogUtil.d(Constants.DEBUG_TAG,"Start locating...");//熄屏状态下Activity会立即执行onStop，不要在onStop中销毁LocationClient，否则onLocationChanged不会被调用，卡在splashActivity
        mLocationClient = new AMapLocationClient(mSplashView.getContext().getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setWifiScan(true);
        mLocationOption.setMockEnable(true);
        mLocationOption.setInterval(1000);
        mLocationOption.setHttpTimeOut(3000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {

                isLocatedSuccess = true;
                mLocatedCity = new LocationEntity();
                mLocatedCity.setAddress(aMapLocation.getAddress());
                mLocatedCity.setStreet(aMapLocation.getStreet());
                mLocatedCity.setDistrict(aMapLocation.getDistrict());
                mLocatedCity.setCity(aMapLocation.getCity());
                mLocatedCity.setProvince(aMapLocation.getProvince());
                mLocationClient.stopLocation();
                LogUtil.d(Constants.DEBUG_TAG,"Get location Succeed!");
                LogUtil.d(Constants.DEBUG_TAG,"Get location: "+mLocatedCity.toString());

                goToNext();

            }else {

                isLocatedSuccess = false;
                mLocationClient.stopLocation();
                LogUtil.d(Constants.DEBUG_TAG,"Get location failed !");
                LogUtil.d(Constants.DEBUG_TAG,"Location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", ErrorInfo:"
                        + aMapLocation.getErrorInfo());
                goToNext();
            }
        }
    }

    private void goToNext(){

        LogUtil.d(Constants.DEBUG_TAG,"Prepared work finished, ready to look for city_id and navigation to next activity.");

        if(isLocatedSuccess){

            locatedSuccess();

        }else {
            locatedFail();
        }


    }

    private void locatedSuccess(){

        mTargetCity = getTargetCity();
        if (mTargetCity == null){//定位成功，未找到定位城市

            mSplashView.toastMessage("定位失败，请选择城市");
            mSplashView.navigationToCityPickActivity();
        }else {
            CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).saveCityId(mTargetCity.getCityId());
            boolean hasDefaultCity = PreferenceUtil.getboolean(mSplashView.getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.HAS_DEFAULT_CITY,false);
            if(!hasDefaultCity){
                PreferenceUtil.putString(mSplashView.getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY_ID,mTargetCity.getCityId());
                PreferenceUtil.putString(mSplashView.getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY,mTargetCity.getDistrictName());

                if(PreferenceUtil.getboolean(mSplashView.getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.IS_NOTI_SHOW,true)){

                    Intent i = new Intent(mSplashView.getContext().getApplicationContext(), NotificationService.class);
                    i.putExtra(Constants.INTENT_KEY_CITY_ID,mTargetCity.getCityId());
                    mSplashView.getContext().startService(i);
                }
            }
            mSplashView.navigationToCityWeatherActivty(mTargetCity);
            if(isCityListDBCreated){
                CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).saveCityId(mTargetCity.getCityId());
            }
        }
    }

    private void locatedFail(){

        boolean hasChosenCity = false;
        if(isCityListDBCreated ){
            hasChosenCity  = CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).loadAllChosenCityId().size() > 0;
        }
        if(isFirstLunch){//第一次启动，定位失败，转到选择城市

            mSplashView.toastMessage("定位失败，请选择城市");
            mSplashView.navigationToCityPickActivity();

        }else {//非第一次启动，定位失败，查看数据库是否有已选城市列表，有就转到pagerActivity，否则转到城市选择页面
            if(hasChosenCity){
                mSplashView.navigationToCityWeatherActivty(mDefaultCity);

            }else {
                mSplashView.navigationToCityPickActivity();
                mSplashView.toastMessage("定位失败，请选择城市");
            }
        }
    }

    private CityEntity getTargetCity(){

        LogUtil.d(Constants.DEBUG_TAG,"Get location successful, looking for match district from database...");

        //先根据districtName 寻找对应的数据
        List<CityEntity> cities = CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).loadDistrict(mLocatedCity.getDistrict().substring(0,mLocatedCity.getDistrict().length()-1));

        for (CityEntity cityEntity: cities){
            if (cityEntity.getProvinceName().contains(mLocatedCity.getProvince()) || mLocatedCity.getProvince().contains(cityEntity.getProvinceName())){
                if (cityEntity.getCityName().contains(mLocatedCity.getCity()) || mLocatedCity.getCity().contains(cityEntity.getCityName())){
                    if (cityEntity.getDistrictName().contains(mLocatedCity.getDistrict()) || mLocatedCity.getDistrict().contains(cityEntity.getDistrictName())){
                        mTargetCity = cityEntity;
                        mTargetCity.setStreetName(mLocatedCity.getStreet());
                        return mTargetCity;
                    }
                }
            }
        }
        //如果没有找到，从数据库读出全部数据，再次寻找，此处为主线程，考虑切换子线程操作。
        if (null == mTargetCity) {
            cities = CityListDatabase.getInstance(mSplashView.getContext().getApplicationContext()).loadAllData();
            for (CityEntity cityEntity : cities) {
                if (cityEntity.getProvinceName().contains(mLocatedCity.getProvince()) || mLocatedCity.getProvince().contains(cityEntity.getProvinceName())) {
                    if (cityEntity.getCityName().contains(mLocatedCity.getCity()) || mLocatedCity.getCity().contains(cityEntity.getCityName())) {
                        if (cityEntity.getDistrictName().contains(mLocatedCity.getDistrict()) || mLocatedCity.getDistrict().contains(cityEntity.getDistrictName())) {
                            mTargetCity = cityEntity;
                            mTargetCity.setStreetName(mLocatedCity.getStreet());
                            return mTargetCity;
                        }
                    }
                }
            }
        }
        return null;
    }
}
