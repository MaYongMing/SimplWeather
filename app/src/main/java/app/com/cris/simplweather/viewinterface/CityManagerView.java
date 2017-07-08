package app.com.cris.simplweather.viewinterface;

import android.content.Context;

import java.util.List;

import app.com.cris.simplweather.model.WeatherEntity;

/**
 * Created by Cris on 2017/7/4.
 */

public interface CityManagerView extends BaseView {
    Context getContext();
    void showContent(List<WeatherEntity> list);
}
