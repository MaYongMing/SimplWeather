package app.com.cris.simplweather.presenter;

import android.app.Activity;

import app.com.cris.simplweather.viewinterface.BaseView;

/**
 * Created by Cris on 2017/6/23.
 */

public abstract class BasePresenter {
    abstract  void attachView(BaseView baseView);
    abstract  void start();
    abstract  void stop();

}
