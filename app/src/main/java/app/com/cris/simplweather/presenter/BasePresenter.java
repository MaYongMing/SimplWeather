package app.com.cris.simplweather.presenter;

import android.app.Activity;

/**
 * Created by Cris on 2017/6/23.
 */

public abstract class BasePresenter {
    abstract  void attachView(Activity activity);
    abstract  void start();
    abstract  void stop();
}
