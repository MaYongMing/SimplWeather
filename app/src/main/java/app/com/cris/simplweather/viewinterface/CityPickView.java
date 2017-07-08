package app.com.cris.simplweather.viewinterface;

import android.content.Context;

import java.util.List;

import app.com.cris.simplweather.model.CityEntity;

/**
 * Created by Cris on 2017/6/25.
 */

public interface CityPickView extends BaseView {
    void deliveryCityList(List<CityEntity> list);
    Context getContext();
    void toastMessage(String msg);

}
