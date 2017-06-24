package app.com.cris.simplweather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import app.com.cris.simplweather.R;

/**
 * Created by Cris on 2017/6/23.
 */

public class CityPickActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
