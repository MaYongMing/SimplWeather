package app.com.cris.simplweather.viewinterface;

import android.app.Activity;
import android.content.Context;

import app.com.cris.simplweather.model.CityEntity;

/**
 * Created by Cris on 2017/6/21.
 */

public interface SplashView extends BaseView{
    Context getContext();
    void navigationToCityWeatherActivty(CityEntity cityEntity);
    void navigationToCityPickActivity();
    void toastMessage(String msg);
}
