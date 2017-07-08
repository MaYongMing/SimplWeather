package app.com.cris.simplweather.fragment;

import android.content.ComponentName;
import android.content.Context;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Long3;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Map;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.adapter.WeatherInfoAdapter;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.presenter.CityWeatherPresenter;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.viewinterface.WeatherView;
import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * Created by Cris on 2017/6/28.
 */

public class CityWeatherFragment extends Fragment implements WeatherView, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{


    private String mTargetCityId;
    private CityWeatherPresenter mPresenter;

    private WeatherInfoAdapter mWeatherInfoAdapter;
    private WeatherEntity mWeatherEntity;



    @BindView(R.id.activity_weather_main_info)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.activity_weather_main_retry)
    protected RelativeLayout mRetryLayout;
    @BindView(R.id.weather_retry_txt)
    protected TextView mRetryTxt;
    @BindView(R.id.weather_retry_btn)
    protected Button mRetryButton;

    @BindView(R.id.activity_weather_main_title_icon)
    protected ImageView mMainTitleIcon;
    @BindView(R.id.activity_weather_main_title_text)
    protected TextView mMainTitleText;
    @BindView(R.id.activity_weather_main_title)
    protected LinearLayout mMainTitle;
    @BindView(R.id.activity_weather_main_title_divider)
    View mTitleDivider;
    @BindView(R.id.activity_weather_main_swipe_refresh)
    protected SwipeRefreshLayout mSwiper;

    public static CityWeatherFragment newInstance(String cityId){
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_KEY_CITY_ID,cityId);
        CityWeatherFragment fragment = new CityWeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mTargetCityId =getArguments().getString(Constants.INTENT_KEY_CITY_ID);
        mPresenter = new CityWeatherPresenter();
        mPresenter.attachView(this);
        mPresenter.deliverTargetCity(mTargetCityId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_main_weather,container,false);
        ButterKnife.bind(this,v);
        initView();
        // mPresenter.start();
        return v;
    }

    private void initView(){

        mRetryButton.setOnClickListener(this);
        mSwiper.setOnRefreshListener(this);
        mSwiper.setDistanceToTriggerSync(300);
        mSwiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void showContent(WeatherEntity weatherEntity) {
        mSwiper.setRefreshing(false);
        mWeatherEntity = weatherEntity;
        mMainTitle.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWeatherInfoAdapter = new WeatherInfoAdapter(getActivity(), mWeatherEntity);
        mRecyclerView.setAdapter(mWeatherInfoAdapter);
        mMainTitleIcon.setImageResource(getCondIconId(weatherEntity.getConCode()));
        mMainTitleText.setText(weatherEntity.getCityName() + " | " + weatherEntity.getCurTmp() + "℃");
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            int mScrolledDistance ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (( (LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0){
                    mScrolledDistance = 0;
                }
                mScrolledDistance += dy;
                if (mScrolledDistance > dpToPx(164)) {
                    int alpha = 255 * (mScrolledDistance - 584 + dpToPx(64)) / dpToPx(64) ;
                    mMainTitle.setVisibility(View.VISIBLE);

                    if(alpha<255){
                        mMainTitle.setBackgroundColor(Color.argb(alpha,255,255,255));
                        mMainTitleIcon.setImageAlpha(alpha);
                        mMainTitleText.setAlpha(alpha/255.0f);
                        mTitleDivider.setAlpha(0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                            getActivity().getWindow().getDecorView().setSystemUiVisibility(option );
                        }
                    }else {

                        mMainTitle.setBackgroundColor(Color.argb(255,255,255,255));
                        mMainTitleIcon.setImageAlpha(255);
                        mMainTitleText.setAlpha(1);
                        mTitleDivider.setAlpha(1);
                    }
                }
                if (mScrolledDistance < dpToPx(164)) {

                    mMainTitle.setVisibility(View.GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE ;
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(option);
                    }
                }


            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
        // mUnbinder.unbind();
    }

    @Override
    public void toastMessage(String msg) {

        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weather_retry_btn:
                mPresenter.stop();
                mPresenter.start();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.stop();
        mPresenter.start();

    }

    @Override
    public void onRefresh() {
        mPresenter.stop();
        mPresenter.start();
    }


    @Override
    public void showLoading() {
        mSwiper.setRefreshing(true);
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


    private int getCondIconId(String code){

        Calendar calendar = Calendar.getInstance();
        int hour =  calendar.get(Calendar.HOUR_OF_DAY);
        if(hour > 6 && hour < 18){

            switch (Integer.parseInt(code)){
                case  100:
                    return R.drawable.c_sunny;
                case  101:
                case  102:
                case  103:
                    return R.drawable.c_cloudt;
                case  104:
                    return R.drawable.c_overcast;
                case 201:
                case 202:
                case 203:
                case 204:
                case 205:
                case 206:
                case 207:
                case 208:
                case 209:
                case 210:
                case 211:
                case 212:
                case 213:
                    return R.drawable.c_hurricane;
                case  300:
                case  301:
                    return R.drawable.c_shower_day;
                case  302:
                case  303:
                    return R.drawable.c_thund_shower;
                case  304:
                    return R.drawable.c_hail;
                case  305:
                case  309:
                    return R.drawable.c_rain_little;
                case  306:
                case  307:
                    return R.drawable.c_rain_middle;
                case  308:
                case  312:
                    return R.drawable.c_rain_storm;
                case  310:
                case  311:
                    return R.drawable.c_rain_heavy;
                case 313:
                case 404:
                case 405:
                case 406:
                    return R.drawable.c_snow_rain;
                case  400:
                case  407:
                    return R.drawable.c_snow_little;
                case  401:
                case  402:
                    return R.drawable.c_snow_middle;
                case  403:
                    return R.drawable.c_snow_storm;
                case  500:
                case  501:
                    return R.drawable.c_fog;
                case  502:
                    return R.drawable.c_19;
                case  503:
                case  504:
                    return R.drawable.c_yangchen;
                case  507:
                case  508:
                    return R.drawable.c_sand;
                case  900:
                    return R.drawable.c_sunny;
                case  901:
                    return R.drawable.c_snow_little;
            }
        }else {

            switch (Integer.parseInt(code)) {
                case  100:
                    return R.drawable.d_suny;
                case  101:
                case  102:
                case  103:
                    return R.drawable.d_cloudy;
                case  104:
                    return R.drawable.c_overcast;
                case 201:
                case 202:
                case 203:
                case 204:
                case 205:
                case 206:
                case 207:
                case 208:
                case 209:
                case 210:
                case 211:
                case 212:
                case 213:
                    return R.drawable.c_hurricane;
                case  300:
                case  301:
                    return R.drawable.d_shower;
                case  302:
                case  303:
                    return R.drawable.c_thund_shower;
                case  304:
                    return R.drawable.c_hail;
                case  305:
                case  309:
                    return R.drawable.c_rain_little;
                case  306:
                case  307:
                    return R.drawable.c_rain_middle;
                case  308:
                case  312:
                    return R.drawable.c_rain_storm;
                case  310:
                case  311:
                    return R.drawable.c_rain_heavy;
                case 313:
                case 404:
                case 405:
                case 406:
                    return R.drawable.c_snow_rain;
                case  400:
                case  407:
                    return R.drawable.d_snow_shower;
                case  401:
                case  402:
                    return R.drawable.c_snow_middle;
                case  403:
                    return R.drawable.c_snow_storm;
                case  500:
                case  501:
                    return R.drawable.d_fog;
                case  502:
                    return R.drawable.d_sand;
                case  503:
                case  504:
                    return R.drawable.d_sand;
                case  507:
                case  508:
                    return R.drawable.c_sand;
                case  900:
                    return R.drawable.c_sunny;
                case  901:
                    return R.drawable.c_snow_little;
            }

        }
        return R.mipmap.ic_launcher;
    }


    private  int dpToPx(float dpValue){
        float density  = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue*density + 0.5f);
    }
}
