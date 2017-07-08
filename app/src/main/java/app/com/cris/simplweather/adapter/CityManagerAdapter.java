package app.com.cris.simplweather.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.activity.WeatherPagerActivity;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;

/**
 * Created by Cris on 2017/7/4.
 */

public class CityManagerAdapter extends RecyclerView.Adapter {

    private int mCheckedCount = 0;
    private List<WeatherEntity> mChosenCitiesWeather;
    private List<ChosenCityHolder> mHolders;
    private Context mContext;

    public CityManagerAdapter(Context context, List<WeatherEntity> weatherEntities){
        mChosenCitiesWeather = weatherEntities;
        mHolders = new ArrayList<>();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_manager_item,parent,false);

        return new ChosenCityHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ChosenCityHolder)  holder).bindView(position);
        mHolders.add(position, (ChosenCityHolder) holder) ;

    }


    @Override
    public int getItemCount() {
        return mChosenCitiesWeather.size();
    }

    public void showCheckBox(){
        for (ChosenCityHolder holder: mHolders){
            holder.showCheckBox();
        }
    }

    public void hideCheckBox(){
        for (ChosenCityHolder holder: mHolders){
            holder.hideCheckBox();
            holder.setUnChecked();
        }
    }

    public void resetCheckCount(){
       mCheckedCount = 0;
    }

    public List<String> getCheckedCityId(){
        List<String> checkedCityIds = new ArrayList<>();
        for (ChosenCityHolder holder: mHolders){
            if(  holder.getCheckStatus()){
                checkedCityIds.add(mChosenCitiesWeather.get(mHolders.indexOf(holder)).getCityId());
            }
        }
        return checkedCityIds;
    }


    private class ChosenCityHolder extends RecyclerView.ViewHolder implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

        private CheckBox mCheckBox;
        private TextView mCityNameTv;
        private ImageView mCondIcon;
        private TextView mCondText;
        private TextView mTmpText;
        private String mCityId;
        public ChosenCityHolder(View itemView) {

            super(itemView);

            mCheckBox = (CheckBox) itemView.findViewById(R.id.city_manager_item_check_box);
            mCityNameTv = (TextView) itemView.findViewById(R.id.city_manager_item_name);
            mCondIcon = (ImageView) itemView.findViewById(R.id.city_manager_item_content_icon);
            mCondText = (TextView) itemView.findViewById(R.id.city_manager_item_content_con);
            mTmpText = (TextView) itemView.findViewById(R.id.city_manager_item_content_tmp);
            itemView.setOnClickListener(this);
            mCheckBox.setOnCheckedChangeListener(this);

        }

        public void bindView(int pos){
            if(null != mChosenCitiesWeather.get(pos)){
                mCityNameTv.setText(mChosenCitiesWeather.get(pos).getCityName());
                mCondIcon.setImageResource(getCondIconId(mChosenCitiesWeather.get(pos).getConCode()));
                mCondText.setText(mChosenCitiesWeather.get(pos).getCurCond());
                mTmpText.setText(mChosenCitiesWeather.get(pos).getCurTmp()+" ℃");
                mCityId = mChosenCitiesWeather.get(pos).getCityId();
                if(pos == 0){
                    mCheckBox.setEnabled(false);
                    mCityNameTv.setText(mChosenCitiesWeather.get(pos).getCityName()+ "(默认)");
                }
            }
        }

        public boolean getCheckStatus(){
            return mCheckBox.isChecked();
        }

        public void showCheckBox(){
            mCheckBox.setVisibility(View.VISIBLE);
        }

        public void hideCheckBox(){
            mCheckBox.setVisibility(View.GONE);
        }
        public void setUnChecked(){
            mCheckBox.setChecked(false);
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

        @Override
        public void onClick(View v) {
            Intent i = new Intent(mContext, WeatherPagerActivity.class);
            i.putExtra(Constants.INTENT_KEY_CITY_ID,mCityId);
            mContext.startActivity(i);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                mCheckedCount++;
                if(mCheckedCount == mChosenCitiesWeather.size()){
                   Toast.makeText(mContext.getApplicationContext(),"请至少保留一个城市！", Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                    mCheckedCount--;
                }
            }else {
                mCheckedCount--;
            }

            LogUtil.d(Constants.DEBUG_TAG,"checkedCount" + mCheckedCount);

        }
    }
}
