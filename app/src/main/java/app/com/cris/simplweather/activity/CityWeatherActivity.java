package app.com.cris.simplweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.adapter.WeatherInfoAdapter;

import app.com.cris.simplweather.model.WeatherEntity;

import app.com.cris.simplweather.presenter.CityWeatherPresenter;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.viewinterface.WeatherView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import okhttp3.Interceptor;

import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Cris on 2017/6/23.
 */

public class CityWeatherActivity extends Activity implements WeatherView,View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{


    private static final int MSG_BACK_PRESSED = 0;

    private boolean mPreBackPressed = false;

    private String mTargetCityId;

    private Unbinder mUnbinder;
    private CityWeatherPresenter mPresenter;
    private WeatherInfoHandler mHandler;
    private WeatherInfoAdapter mWeatherInfoAdapter;

    @BindView(R.id.activity_weather_main_info)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.activity_weather_main_retry)
    protected RelativeLayout mRetryLayout;
    @BindView(R.id.weather_retry_txt)
    protected TextView mRetryTxt;
    @BindView(R.id.weather_retry_btn)
    protected Button mRetryButton;
    @BindView(R.id.activity_weather_main_tab)
    protected RelativeLayout mTabLayout;
    @BindView(R.id.tab_add_city)
    protected ImageButton mTabAddCity;
    @BindView(R.id.tab_setting)
    protected ImageButton mTabSetting;
    @BindView(R.id.tab_pager_dot)
    protected LinearLayout mTabPagerIndicator;
    @BindView(R.id.activity_weather_main_swipe_refresh)
    protected SwipeRefreshLayout mSwiper;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);
        mUnbinder = ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 21) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().getDecorView().setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mTargetCityId = getIntent().getStringExtra(Constants.INTENT_KEY_CITY_ID);
       // mTargetStreetName = getIntent().getStringExtra(Constants.INTENT_KEY_STREET_NAME);


        initView();

        mHandler = new WeatherInfoHandler();
        mPresenter = new CityWeatherPresenter();
        mPresenter.attachView(this);
        mPresenter.deliverTargetCity(mTargetCityId);
        mPresenter.start();

    }
    private void initView(){
        mRetryButton.setOnClickListener(this);
        mTabAddCity.setOnClickListener(this);
        mTabSetting.setOnClickListener(this);
        mSwiper.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mSwiper.setDistanceToTriggerSync(300);
        mSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void showContent(WeatherEntity weatherEntity) {

            mSwiper.setRefreshing(false);
            mRecyclerView.setVisibility(View.VISIBLE);

            mWeatherInfoAdapter = new WeatherInfoAdapter(this, weatherEntity);
            mRecyclerView.setAdapter(mWeatherInfoAdapter);

    }

    @Override
    public void showLoading() {
        mSwiper.setRefreshing(true);
        //mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRetry(String msg) {
        if(mSwiper.isRefreshing()){
            mSwiper.setRefreshing(false);
        }
        mRetryLayout.setVisibility(View.VISIBLE);
        if (null != msg){
            mRetryTxt.setText(msg);
        }

    }

    @Override
    public void hideLoading() {
        mSwiper.setRefreshing(false);
    }

    @Override
    public void hideRetry() {
        mRetryLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!mPreBackPressed){
            toastMessage("退出请再按一次返回键~");
            mPreBackPressed = true;
            mHandler.sendEmptyMessageDelayed(MSG_BACK_PRESSED, 2000);

        }else {
            finish();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void toastMessage(String msg) {

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.weather_retry_btn:
                mPresenter.stop();
                mPresenter.start();
                break;

            case R.id.tab_add_city:
                intent = new Intent(this,CityManagerActivity.class);
                startActivity(intent);
                break;

            case R.id.tab_setting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {

        mPresenter.stop();
        mPresenter.start();

    }


    private class WeatherInfoHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case MSG_BACK_PRESSED:
                    mPreBackPressed = false;
                    break;

                default:
                    break;
            }
        }
    }



    class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException {

            long t1 = System.nanoTime();
            Request request = chain.request();
            LogUtil.d(Constants.DEBUG_TAG, "url is "+request.url().toString());
            Response response = chain.proceed(request);

            long t2 = System.nanoTime();


            return response;
        }
    }

}

