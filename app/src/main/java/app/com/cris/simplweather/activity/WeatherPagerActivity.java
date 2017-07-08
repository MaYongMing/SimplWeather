package app.com.cris.simplweather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.adapter.WeatherPagerAdapter;
import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.fragment.CityWeatherFragment;
import app.com.cris.simplweather.utils.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cris on 2017/6/28.
 */

public class WeatherPagerActivity extends AppCompatActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private static final int MSG_BACK_PRESSED = 0;


    @BindView(R.id.activity_weather_pager)
    ViewPager mWeatherPager;
    @BindView(R.id.activity_weather_main_tab)
    protected RelativeLayout mTabLayout;
    @BindView(R.id.tab_add_city)
    protected ImageButton mTabAddCity;
    @BindView(R.id.tab_setting)
    protected ImageButton mTabSetting;
    @BindView(R.id.tab_pager_dot)
    protected LinearLayout mTabPagerIndicator;


    private FragmentManager mFragmentManager;
    private WeatherPagerAdapter mPagerAdapter;
    private List<String> mCityIdList;
    private WeatherInfoHandler mHandler;
    private boolean mPreBackPressed = false;


    private List<CityWeatherFragment> mFragementList;
    private String mTargetCityId;
    private int cityCount;
    private List<ImageView> mIndicators;

    private boolean isCitiesChanged;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_pager);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= 21) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().getDecorView().setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mHandler = new WeatherInfoHandler();
        initView();
        addPagerIndicator(cityCount);
        mIndicators.get(mWeatherPager.getCurrentItem()).setImageResource(R.drawable.round_dot_focused);

    }

    @Override
    protected void onNewIntent(Intent intent) {

        mTargetCityId = intent.getStringExtra(Constants.INTENT_KEY_CITY_ID);

        if(mTargetCityId !=null ) {

            if (mCityIdList.contains(mTargetCityId)) {
                int index = mCityIdList.indexOf(mTargetCityId);
                mWeatherPager.setCurrentItem(index);
            } else {
                CityListDatabase.getInstance(getApplicationContext()).saveCityId(mTargetCityId);
                mCityIdList.add( mTargetCityId);
                mFragementList.add(CityWeatherFragment.newInstance(mTargetCityId));
                addPagerIndicator(1);
                mPagerAdapter.notifyDataSetChanged();
                mWeatherPager.setCurrentItem(mFragementList.size()-1);
            }
            cityCount = mFragementList.size();
        }

        isCitiesChanged = intent.getBooleanExtra(Constants.INTENT_KEY_CITIES_CHANGED,false);

        if(isCitiesChanged){
            mCityIdList.clear();
            mFragementList.clear();
            mIndicators.clear();
            mCityIdList = CityListDatabase.getInstance(getApplicationContext()).loadAllChosenCityId();

            for (int i = 0; i< mCityIdList.size();  i++){

                mFragementList.add(CityWeatherFragment.newInstance(mCityIdList.get(i)));
            }

            cityCount = mFragementList.size();
            mTabPagerIndicator.removeAllViews();
            addPagerIndicator(cityCount);
            mPagerAdapter.notifyDataSetChanged();
            mIndicators.get(mWeatherPager.getCurrentItem()).setImageResource(R.drawable.round_dot_focused);
        }

        super.onNewIntent(intent);

    }

    private void initView(){

        mTargetCityId = getIntent().getStringExtra(Constants.INTENT_KEY_CITY_ID);

        mCityIdList = CityListDatabase.getInstance(getApplicationContext()).loadAllChosenCityId();
        mIndicators = new ArrayList<>();
        mFragementList = new ArrayList<>();

        if(mCityIdList.contains(mTargetCityId)) {
            mCityIdList.remove(mTargetCityId);
        }else {
            CityListDatabase.getInstance(getApplicationContext()).saveCityId(mTargetCityId);
        }

        mFragementList.add(CityWeatherFragment.newInstance(mTargetCityId));
        for (int i = 0; i< mCityIdList.size();  i++){

            mFragementList.add(CityWeatherFragment.newInstance(mCityIdList.get(i)));
        }

        mCityIdList.add(0,mTargetCityId);
        cityCount = mFragementList.size();

        mTabAddCity.setOnClickListener(this);
        mTabSetting.setOnClickListener(this);
        mWeatherPager.addOnPageChangeListener(this);

        mFragmentManager=getSupportFragmentManager();
        mPagerAdapter = new WeatherPagerAdapter(mFragmentManager,mFragementList);
        mWeatherPager.setAdapter(mPagerAdapter);

    }


    private void addPagerIndicator(int count){

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(18,18);
        mParams.gravity = Gravity.CENTER_VERTICAL;
        mParams.setMargins(10,10,10,10);

        for (int i=0; i<count; i++){
            ImageView pagerIndicator = new ImageView(this);
            pagerIndicator.setImageResource(R.drawable.round_dot_normal);
            pagerIndicator.setLayoutParams(mParams);
            mIndicators.add(pagerIndicator);
            mTabPagerIndicator.addView(pagerIndicator);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (!mPreBackPressed){
            Toast mToast =      Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            ((TextView) mToast.getView().findViewById(android.R.id.message)).setTextSize(16);
            mToast.show();
            mPreBackPressed = true;
            mHandler.sendEmptyMessageDelayed(MSG_BACK_PRESSED, 2000);

        }else {
            finish();
        }
    }

    private class WeatherInfoHandler extends Handler {
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){

            case R.id.tab_add_city:
                intent = new Intent(this,CityManagerActivity.class);
                startActivity(intent);
                // finish();
                break;

            case R.id.tab_setting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        for (ImageView view:mIndicators){

            if( mIndicators.indexOf(view) == position){
                view.setImageResource(R.drawable.round_dot_focused);
            }
            else view.setImageResource(R.drawable.round_dot_normal);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
