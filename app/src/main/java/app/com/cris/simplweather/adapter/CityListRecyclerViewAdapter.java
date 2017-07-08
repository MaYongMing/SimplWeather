package app.com.cris.simplweather.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.activity.WeatherPagerActivity;
import app.com.cris.simplweather.model.CityEntity;
import app.com.cris.simplweather.service.NotificationService;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.utils.PreferenceUtil;

/**
 * Created by Cris on 2017/6/29.
 */

public class CityListRecyclerViewAdapter extends RecyclerView.Adapter<CityListRecyclerViewAdapter.CityListHolder> {

    private Context mContext;
    private List<CityEntity> mCityList;
    public CityListRecyclerViewAdapter(Context context, List<CityEntity> list){
        mContext = context;
        mCityList = list;
    }

    @Override
    public CityListRecyclerViewAdapter.CityListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_pick_list_item,parent,false);
        return new CityListHolder(v);
    }

    @Override
    public void onBindViewHolder(CityListRecyclerViewAdapter.CityListHolder holder, int position) {
        holder.bindView(position);

    }

    @Override
    public int getItemCount() {
        return mCityList.size();
    }



   public  class CityListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTextView;
       private CityEntity mCityEntity;
        public CityListHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.city_pick_list_text);
            itemView.setOnClickListener(this);

        }

        public void bindView(int position){
            mCityEntity = mCityList.get(position);

            StringBuilder builder = new StringBuilder();
            String districtName ;
            String cityName;
            String provinceName;

            districtName = mCityList.get(position).getDistrictName();
            cityName = mCityList.get(position).getCityName();
            provinceName = mCityList.get(position).getProvinceName();
            if(districtName.equals(cityName)){
                if (cityName.equals(provinceName)){
                    builder.append(cityName).append(" - 中国");
                }else {
                    builder.append(cityName)
                            .append(" - ")
                            .append(provinceName)
                            .append(", 中国");
                }
            }else if(cityName.equals(provinceName)){
                builder.append(districtName)
                        .append(" - ")
                        .append(cityName)
                        .append(", 中国");

            }
            else {
                builder.append(districtName)
                        .append(" - ")
                        .append(cityName)
                        .append(", ")
                        .append(provinceName)
                        .append(", 中国");
            }
            mTextView.setText(builder.toString());
        }

       @Override
       public void onClick(View v) {

           Intent intent = new Intent(mContext, WeatherPagerActivity.class);

           if(null != mCityEntity){

               boolean hasDefaultCity = PreferenceUtil.getboolean(mContext.getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.HAS_DEFAULT_CITY,false);
               if(!hasDefaultCity){
                   PreferenceUtil.putString(mContext.getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY_ID,mCityEntity.getCityId());
                   PreferenceUtil.putString(mContext.getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY,mCityEntity.getDistrictName());
                   if(PreferenceUtil.getboolean(mContext.getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.IS_NOTI_SHOW,true)) {

                       Intent i = new Intent(mContext.getApplicationContext(), NotificationService.class);
                       i.putExtra(Constants.INTENT_KEY_CITY_ID, mCityEntity.getCityId());
                       mContext.startService(i);
                   }
               }
               intent.putExtra(Constants.INTENT_KEY_CITY_ID,mCityEntity.getCityId());
           }

           mContext.startActivity(intent);
       }
   }

}

