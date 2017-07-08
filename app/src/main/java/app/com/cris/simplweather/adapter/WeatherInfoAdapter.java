package app.com.cris.simplweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.customview.RoundProgressView;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cris on 2017/6/25.
 */

public class WeatherInfoAdapter extends RecyclerView.Adapter  {

    private final int WEATHER_MAIN_BASIC = 1;
    private final int WEATHER_MAIN_THREE_DAYS = 2;
    private final int WEATHER_MAIN_AQI = 3;
    private final int WEATHER_MAIN_SUGGESTION = 4;
    private final int WEATHER_ITEM_COUNT = 4;

    private WeatherEntity mWeatherEntity;
    private Context mContext;

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(holder instanceof  WeatherBasicHolder){
            WeatherBasicHolder basicHolder  = (WeatherBasicHolder) holder;
            //basicHolder.startAnimation();
        }
    }

    public WeatherInfoAdapter(Context context, WeatherEntity weatherEntity){
        mContext = context;
        mWeatherEntity = weatherEntity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        switch (viewType){

            case WEATHER_MAIN_BASIC:
                view = inflater.inflate(R.layout.weather_main_basic,parent,false);
                return new WeatherBasicHolder(view);
            case WEATHER_MAIN_THREE_DAYS:
                view = inflater.inflate(R.layout.weather_main_three_days,parent,false);
                return new WeatherThreeDaysHolder(view);
            case WEATHER_MAIN_AQI:
                view = inflater.inflate(R.layout.weather_main_aqi,parent,false);
                return new WeatherAqiHolder(view);
            case WEATHER_MAIN_SUGGESTION:
                view = inflater.inflate(R.layout.weather_main_suggestion,parent,false);
                return new WeatherSuggestionHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)){

            case WEATHER_MAIN_BASIC:
                WeatherBasicHolder basicHolder = (WeatherBasicHolder) holder;
                basicHolder.bindView();
                break;

            case WEATHER_MAIN_THREE_DAYS:
                WeatherThreeDaysHolder threeDaysHolder = (WeatherThreeDaysHolder) holder;
                threeDaysHolder.bindView();
                break;

            case WEATHER_MAIN_AQI:
                WeatherAqiHolder aqiHolder = (WeatherAqiHolder) holder;
                aqiHolder.bindView();
                break;

            case WEATHER_MAIN_SUGGESTION:
                WeatherSuggestionHolder suggestionHolder = (WeatherSuggestionHolder) holder;
                suggestionHolder.bindView();
                break;

            default:
                break;

        }

    }

    @Override
    public int getItemCount() {

        return WEATHER_ITEM_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0:
                return WEATHER_MAIN_BASIC;
            case 1:
                return WEATHER_MAIN_THREE_DAYS;
            case 2:
                return WEATHER_MAIN_AQI;
            case 3:
                return WEATHER_MAIN_SUGGESTION;
            default:
                return position;
        }
    }


    protected class WeatherBasicHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.weather_basic_address)
        protected TextView mAddress;
        @BindView(R.id.weather_basic_condition)
        protected TextView mCurCondition;
        @BindView(R.id.weather_basic_degree)
        protected TextView mTmpDegree;
        @BindView(R.id.weather_basic_degree_feelingval)
        protected TextView mFeelTmpDegree;
        @BindView(R.id.weather_basic_humidity_value)
        protected TextView mHumidity;
        @BindView(R.id.weather_basic_wind_dir)
        protected TextView mWindir;
        @BindView(R.id.weather_basic_wind_speed)
        protected TextView mWindLvl;
        @BindView(R.id.weather_basic_icon)
        protected ImageView mCondIcon;
        @BindView(R.id.weather_basic_anim)
        protected ImageView mAnimationImage;


        public WeatherBasicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(){
            mAddress.setText(mWeatherEntity.getCityName());
            mCurCondition.setText(mWeatherEntity.getCurCond());
            mTmpDegree.setText(mWeatherEntity.getCurTmp()+ "℃");
            mFeelTmpDegree.setText(mWeatherEntity.getFeelTmp()+ "℃");
            mHumidity.setText(mWeatherEntity.getHumidity());
            mWindir.setText(mWeatherEntity.getWindDir());
            mWindLvl.setText(mWeatherEntity.getWindLvl());
            mCondIcon.setImageResource(getCondIconId(mWeatherEntity.getConCode()));
            //mAnimationImage.setImageResource(R.drawable.thunder_lightning1);
        }

        public  void startAnimation(){
            Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.thounder_1);
            mAnimationImage.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    mAnimationImage.startAnimation(animation);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

    }

    protected class WeatherThreeDaysHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.weather_three_days_1_con)
        TextView mFirDayCon;
        @BindView(R.id.weather_three_days_1_deg)
        TextView mFirDayTmp;
        @BindView(R.id.weather_three_days_2_con)
        TextView mSecDayCon;
        @BindView(R.id.weather_three_days_2_deg)
        TextView mSecDayTmp;
        @BindView(R.id.weather_three_days_3_con)
        TextView mThirdDayCon;
        @BindView(R.id.weather_three_days_3_deg)
        TextView mThirdDayTmp;


        public WeatherThreeDaysHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(){
            mFirDayCon.setText(mWeatherEntity.getDailyForecasts().get(0).getCond().getTxt_d());
            mFirDayTmp.setText(mWeatherEntity.getDailyForecasts().get(0).getTmp().getMin() + " ~ " + mWeatherEntity.getDailyForecasts().get(0).getTmp().getMax()+"℃");
            mSecDayCon.setText(mWeatherEntity.getDailyForecasts().get(1).getCond().getTxt_d());
            mSecDayTmp.setText(mWeatherEntity.getDailyForecasts().get(1).getTmp().getMin() + " ~ " + mWeatherEntity.getDailyForecasts().get(0).getTmp().getMax()+"℃");
            mThirdDayCon.setText(mWeatherEntity.getDailyForecasts().get(2).getCond().getTxt_d());
            mThirdDayTmp.setText(mWeatherEntity.getDailyForecasts().get(2).getTmp().getMin() + " ~ " + mWeatherEntity.getDailyForecasts().get(0).getTmp().getMax()+"℃");
        }

    }

    protected class WeatherAqiHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.weather_aqi_percent_main)
        RoundProgressView mMainAqi;
        @BindView(R.id.weather_aqi_percent_2_5)
        RoundProgressView mPM25Aqi;


        public WeatherAqiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindView(){
            mMainAqi.setTitle(mWeatherEntity.getAirQlty());
            mPM25Aqi.setTitle(mWeatherEntity.getAirQlty());
            mMainAqi.setMaxValue(450);
            mMainAqi.setCurrentValue(Integer.parseInt(mWeatherEntity.getAqi()));
            mPM25Aqi.setMaxValue(450);
            mPM25Aqi.setCurrentValue(Integer.parseInt(mWeatherEntity.getPm2p5()));
        }
    }
    protected class WeatherSuggestionHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.weather_suggestion_cloth)
        LinearLayout mClothLayout;
        @BindView(R.id.weather_suggestion_trav)
        LinearLayout mTravLayout;
        @BindView(R.id.weather_suggestion_sport)
        LinearLayout mSportLayout;
        @BindView(R.id.weather_suggestion_uv)
        LinearLayout mUvLayout;
        @BindView(R.id.weather_suggestion_cw)
        LinearLayout mCarWashLayout;

        private ImageView mClothIcon;
        private TextView mClothTitle;
        private TextView mClothContent;
        private ImageView mTravIcon;
        private TextView mTravTitle;
        private TextView mTravContent;
        private ImageView mSportIcon;
        private TextView mSportTitle;
        private TextView mSportContent;
        private ImageView mUvIcon;
        private TextView mUvTitle;
        private TextView mUvContent;
        private ImageView mCwIcon;
        private TextView mCwTitle;
        private TextView mCwContent;


        public WeatherSuggestionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindView(){

            mClothIcon = (ImageView) mClothLayout.findViewById(R.id.weather_suggestion_item_icon);
            mClothTitle = (TextView) mClothLayout.findViewById(R.id.weather_suggestion_item_title);
            mClothContent = (TextView) mClothLayout.findViewById(R.id.weather_suggestion_item_content);

            mTravIcon = (ImageView) mTravLayout.findViewById(R.id.weather_suggestion_item_icon);
            mTravTitle = (TextView) mTravLayout.findViewById(R.id.weather_suggestion_item_title);
            mTravContent = (TextView) mTravLayout.findViewById(R.id.weather_suggestion_item_content);

            mSportIcon = (ImageView) mSportLayout.findViewById(R.id.weather_suggestion_item_icon);
            mSportTitle = (TextView) mSportLayout.findViewById(R.id.weather_suggestion_item_title);
            mSportContent = (TextView) mSportLayout.findViewById(R.id.weather_suggestion_item_content);

            mUvIcon = (ImageView) mUvLayout.findViewById(R.id.weather_suggestion_item_icon);
            mUvTitle = (TextView) mUvLayout.findViewById(R.id.weather_suggestion_item_title);
            mUvContent = (TextView) mUvLayout.findViewById(R.id.weather_suggestion_item_content);

            mCwIcon = (ImageView) mCarWashLayout.findViewById(R.id.weather_suggestion_item_icon);
            mCwTitle = (TextView) mCarWashLayout.findViewById(R.id.weather_suggestion_item_title);
            mCwContent = (TextView) mCarWashLayout.findViewById(R.id.weather_suggestion_item_content);

            mClothIcon.setImageResource(R.drawable.ic_clothes_level);
            mClothIcon.setBackgroundResource(R.drawable.bg_ic_cloth);
            mClothTitle.setText(mWeatherEntity.getSuggestion().getDrsg().getBrf());
            mClothContent.setText(mWeatherEntity.getSuggestion().getDrsg().getTxt());

            mTravIcon.setImageResource(R.drawable.ic_visibility_level);
            mTravIcon.setBackgroundResource(R.drawable.bg_ic_trav);
            mTravTitle.setText(mWeatherEntity.getSuggestion().getTrav().getBrf());
            mTravContent.setText(mWeatherEntity.getSuggestion().getTrav().getTxt());

            mSportIcon.setImageResource(R.drawable.ic_sport_level);
            mSportIcon.setBackgroundResource(R.drawable.bg_ic_sport);
            mSportTitle.setText(mWeatherEntity.getSuggestion().getSport().getBrf());
            mSportContent.setText(mWeatherEntity.getSuggestion().getSport().getTxt());

            mUvIcon.setImageResource(R.drawable.ic_uv_levle);
            mUvIcon.setBackgroundResource(R.drawable.bg_ic_uv);
            mUvTitle.setText(mWeatherEntity.getSuggestion().getUv().getBrf());
            mUvContent.setText(mWeatherEntity.getSuggestion().getUv().getTxt());

            mCwIcon.setImageResource(R.drawable.ic_car_level);
            mCwIcon.setBackgroundResource(R.drawable.bg_ic_cw);
            mCwTitle.setText(mWeatherEntity.getSuggestion().getCw().getBrf());
            mCwContent.setText(mWeatherEntity.getSuggestion().getCw().getTxt());
        }
    }

//    private int getAnimationimage(String code) {
//
//        switch (Integer.parseInt(code)) {
//            case 100:
//                return R.drawable.sunny;
//            case 101:
//                return R.drawable.cloudy3;
//            case 102:
//                return R.drawable.cloudy1;
//            case 103:
//                return R.drawable.cloudy2;
//            case 104:
//                return R.drawable.overcast;
//            case 201:
//            case 202:
//            case 203:
//            case 204:
//            case 205:
//            case 206:
//            case 207:
//            case 208:
//            case 209:
//            case 210:
//            case 211:
//            case 212:
//            case 213:
//                return R.drawable.wind;
//            case 300:
//                return R.drawable.shower1;
//            case 301:
//                return R.drawable.shower2;
//            case 302:
//                return R.drawable.tstorm1;
//            case 303:
//                return R.drawable.tstorm3;
//            case 304:
//                return R.drawable.hail;
//            case 305:
//            case 309:
//                return R.drawable.light_rain;
//            case 306:
//            case 307:
//            case 308:
//            case 310:
//            case 311:
//            case 312:
//                return R.drawable.shower3;
//            case 313:
//            case 404:
//            case 405:
//            case 406:
//                return R.drawable.sleet;
//            case 400:
//            case 407:
//                return R.drawable.snow1;
//            case 401:
//            case 402:
//                return R.drawable.snow4;
//            case 403:
//                return R.drawable.snow5;
//            case 500:
//            case 501:
//                return R.drawable.fog;
//            case 502:
//                return R.drawable.heavy_fog;
//            case 503:
//            case 504:
//                return R.drawable.sand;
//            case 507:
//            case 508:
//                return R.drawable.heavy_fog;
//            case 900:
//                return R.drawable.sunny;
//            case 901:
//                return R.drawable.snow5;
//
//        }
//
//    }

    private int getCondIconId(String code){

        Calendar calendar = Calendar.getInstance();
        int hour =  calendar.get(Calendar.HOUR_OF_DAY);
        if(hour > 6 && hour < 18){

            switch (Integer.parseInt(code)){
                case  100:
                    return R.drawable.sunny;
                case  101:
                    return R.drawable.cloudy3;
                case  102:
                    return R.drawable.cloudy1;
                case  103:
                    return R.drawable.cloudy2;
                case  104:
                    return R.drawable.overcast;
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
                    return R.drawable.wind;
                case  300:
                    return R.drawable.shower1;
                case  301:
                    return R.drawable.shower2;
                case  302:
                    return R.drawable.tstorm1;
                case  303:
                    return R.drawable.tstorm3;
                case  304:
                    return R.drawable.hail;
                case  305:
                case  309:
                    return R.drawable.light_rain;
                case  306:
                case  307:
                case  308:
                case  310:
                case  311:
                case  312:
                    return R.drawable.shower3;
                case 313:
                case 404:
                case 405:
                case 406:
                    return R.drawable.sleet;
                case  400:
                case  407:
                    return R.drawable.snow1;
                case  401:
                case  402:
                    return R.drawable.snow4;
                case  403:
                    return R.drawable.snow5;
                case  500:
                case  501:
                    return R.drawable.fog;
                case  502:
                    return R.drawable.heavy_fog;
                case  503:
                case  504:
                    return R.drawable.sand;
                case  507:
                case  508:
                    return R.drawable.heavy_fog;
                case  900:
                    return R.drawable.sunny;
                case  901:
                    return R.drawable.snow5;
            }
        }else {

            switch (Integer.parseInt(code)) {
                case 100:
                    return R.drawable.sunny_night;
                case 101:
                    return R.drawable.cloudy3_night;
                case 102:
                    return R.drawable.cloudy1_night;
                case 103:
                    return R.drawable.cloudy2_night;
                case 104:
                    return R.drawable.overcast;
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
                    return R.drawable.wind;
                case 300:
                    return R.drawable.shower1_night;
                case 301:
                    return R.drawable.shower2_night;
                case 302:
                    return R.drawable.tstorm1_night;
                case 303:
                    return R.drawable.tstorm3;
                case 304:
                    return R.drawable.hail;
                case 305:
                case 309:
                    return R.drawable.light_rain;
                case 306:
                case 307:
                case 308:
                case 310:
                case 311:
                case 312:
                    return R.drawable.shower3;
                case 313:
                case 404:
                case 405:
                case 406:
                    return R.drawable.sleet;
                case 400:
                case 407:
                    return R.drawable.snow1_night;
                case 401:
                case 402:
                    return R.drawable.snow4;
                case 403:
                    return R.drawable.snow5;
                case 500:
                case 501:
                    return R.drawable.fog_night;
                case 502:
                    return R.drawable.heavy_fog;
                case 503:
                case 504:
                    return R.drawable.sand;
                case 507:
                case 508:
                    return R.drawable.heavy_fog;
                case 900:
                    return R.drawable.sunny_night;
                case 901:
                    return R.drawable.snow5;
            }

        }
        return R.mipmap.ic_launcher;
    }
}

