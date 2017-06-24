package app.com.cris.simplweather.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.utils.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cris on 2017/6/23.
 */

public class CityWeatherActivity extends Activity {

    private String mTargetCityId;
    private String mTargetStreetName;

    @BindView(R.id.weather_main_address)
    TextView tv_address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);
        ButterKnife.bind(this);

        mTargetCityId = getIntent().getStringExtra(Constants.INTENT_KEY_CITY_ID);
        mTargetStreetName = getIntent().getStringExtra(Constants.INTENT_KEY_STREET_NAME);

        if (Build.VERSION.SDK_INT >= 21) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().getDecorView().setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }
}

