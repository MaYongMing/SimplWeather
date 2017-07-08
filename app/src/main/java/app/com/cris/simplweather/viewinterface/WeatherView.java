package app.com.cris.simplweather.viewinterface;

import android.app.Activity;
import android.content.Context;

import app.com.cris.simplweather.model.WeatherEntity;

/**
 * Created by Cris on 2017/6/21.
 */

public interface WeatherView extends BaseView{
    Context getContext();
    void toastMessage(String msg);
    void showContent(WeatherEntity weatherEntity);
    void showLoading();
    void showRetry(String msg);
    void hideLoading();
    void hideRetry();

}
