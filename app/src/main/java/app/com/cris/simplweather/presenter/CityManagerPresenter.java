package app.com.cris.simplweather.presenter;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.com.cris.simplweather.activity.CityManagerActivity;
import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.DiskCacheManager;
import app.com.cris.simplweather.utils.PreferenceUtil;
import app.com.cris.simplweather.viewinterface.BaseView;
import app.com.cris.simplweather.viewinterface.CityManagerView;

/**
 * Created by Cris on 2017/7/4.
 */

public class CityManagerPresenter extends BasePresenter {
    private CityManagerView mCityManagerView;
    private List<String> mChosenCities;
    private  List<WeatherEntity> mWeatherInfos;
    private Gson mGson;

    @Override
    public void attachView(BaseView baseView) {
        mCityManagerView = (CityManagerView) baseView;
        mWeatherInfos = new ArrayList<>();
    }

    @Override
    public void start() {
        mGson = new Gson();
        mWeatherInfos.clear();
        mChosenCities = CityListDatabase.getInstance(mCityManagerView.getContext().getApplicationContext()).loadAllChosenCityId();
        String defaultCityId = PreferenceUtil.getString(mCityManagerView.getContext().getApplicationContext(), Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY_ID,null);

        if(null != mChosenCities) {
            for (String id : mChosenCities) {
                String str = null;
                WeatherEntity weatherEntity = null;
                try {
                    str = DiskCacheManager.getInstance(mCityManagerView.getContext().getApplicationContext()).getString(id);
                    weatherEntity = mGson.fromJson(str, WeatherEntity.class);
                    if(id.equalsIgnoreCase(defaultCityId) && weatherEntity != null) {
                        mWeatherInfos.add(0,weatherEntity);
                    }else {
                        mWeatherInfos.add(weatherEntity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }


        mCityManagerView.showContent(mWeatherInfos);
    }

    @Override
    public void stop() {

    }

    public void removeChosenCities(List<String> selectedCities){
        for (String cityId: selectedCities){
            CityListDatabase.getInstance(mCityManagerView.getContext().getApplicationContext()).removeCityId(cityId);
        }
    }
}
