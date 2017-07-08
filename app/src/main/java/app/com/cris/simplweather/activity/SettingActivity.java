package app.com.cris.simplweather.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.service.NotificationService;
import app.com.cris.simplweather.service.UpdateService;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.utils.PreferenceUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cris on 2017/6/27.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    @BindView(R.id.activity_setting_switch_location)
    Switch mLocationSwitch;
    @BindView(R.id.activity_setting_switch_notification)
    Switch mNotiSwitch;
    @BindView(R.id.activity_setting_city_default)
    TextView mDefaultCityTxt;
    @BindView(R.id.activity_setting_city_more)
    ImageButton mSetDefCityBtn;
    @BindView(R.id.activity_setting_update_time)
    TextView mUpdateFrequency;
    @BindView(R.id.activity_setting_update_more)
    ImageButton mUpdateSetBtn;
    @BindView(R.id.activity_setting_toolbar)
    Toolbar mToolbar;

    private TextView mFre2H;
    private TextView mFre4H;
    private TextView mFre8H;
    private TextView mFreOff;


    private PopupWindow mUpdateFreWindow;
    private View mPopupContentView;
    private String mSelectedDefaultCity;
    private String mSelectedDefaultCityId;

    private Intent mUpdateServiceIntent;

    private boolean isLocatingOn;
    private boolean isNotificationShow;
    private int mUpdatePeriod;
    private String mDefaultCity;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mUpdateServiceIntent = new Intent(getApplicationContext(), UpdateService.class);
        initView();
    }

    private void initView(){
        isLocatingOn = PreferenceUtil.getboolean(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.IS_LOCATING_ON,true);
        isNotificationShow = PreferenceUtil.getboolean(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.IS_NOTI_SHOW,true);
        mUpdatePeriod =(int) PreferenceUtil.getLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,2);
        mDefaultCity = PreferenceUtil.getString(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.DEFAULT_CITY,null);

        mLocationSwitch.setChecked(isLocatingOn);
        mNotiSwitch.setChecked(isNotificationShow);
        mDefaultCityTxt.setText(mDefaultCity);


//        mPopupContentView = LayoutInflater.from(this).inflate(R.layout.activity_setting_popup,null);
//        mUpdateFreWindow  = new PopupWindow(mPopupContentView);
//        mUpdateFreWindow.setWidth(300);
//        mUpdateFreWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        mUpdateFreWindow.setBackgroundDrawable(new BitmapDrawable());
//        mUpdateFreWindow.setOutsideTouchable(true);
//
//        mFre2H = (TextView) mPopupContentView.findViewById(R.id.update_2h);
//        mFre4H = (TextView) mPopupContentView.findViewById(R.id.update_4h);
//        mFre8H = (TextView) mPopupContentView.findViewById(R.id.update_8h);
//        mFreOff = (TextView) mPopupContentView.findViewById(R.id.update_off);

//        mFre2H.setOnClickListener(this);
//        mFre4H.setOnClickListener(this);
//        mFre8H.setOnClickListener(this);
//        mFreOff.setOnClickListener(this);

        mLocationSwitch.setOnCheckedChangeListener(this);
        mNotiSwitch.setOnCheckedChangeListener(this);
        mSetDefCityBtn.setOnClickListener(this);
        mUpdateSetBtn.setOnClickListener(this);

        switch (mUpdatePeriod){
            case 0:
                mUpdateFrequency.setText("关闭");
                break;
            case 2:
                mUpdateFrequency.setText("2小时");
                break;
            case 4:
                mUpdateFrequency.setText("4小时");
                break;
            case 8:
                mUpdateFrequency.setText("8小时");
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.activity_setting_city_more:

                List<String> cityNames;
                cityNames = CityListDatabase.getInstance(getApplicationContext()).loadAllChosenCityName();

                AlertDialog.Builder mCitybuilder = new AlertDialog.Builder(this);
                final String[] cityArray = cityNames.toArray(new String[cityNames.size()]);

                mCitybuilder.setItems(cityArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedDefaultCity = cityArray[which];

                        if(null != mSelectedDefaultCity){
                            mSelectedDefaultCityId = CityListDatabase.getInstance(getApplicationContext()).getCityId(mSelectedDefaultCity);
                            PreferenceUtil.putString(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY,mSelectedDefaultCity);
                            PreferenceUtil.putString(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY_ID,mSelectedDefaultCityId);
                            PreferenceUtil.putBoolean(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.HAS_DEFAULT_CITY,true);
                            mDefaultCityTxt.setText(mSelectedDefaultCity);
                            LogUtil.d(Constants.DEBUG_TAG,mSelectedDefaultCity+mSelectedDefaultCityId);
                            if(PreferenceUtil.getboolean(getApplicationContext(),Constants.Preferences.PREF_NAME, Constants.Preferences.IS_NOTI_SHOW,true)){
                                Intent i = new Intent(getApplicationContext(), NotificationService.class);
                                i.putExtra(Constants.INTENT_KEY_CITY_ID,mSelectedDefaultCity);
                                startService(i);
                            }

                        }
                    }
                });
                mCitybuilder.create();
                mCitybuilder.show();

                break;

            case R.id.activity_setting_update_more:
//                mUpdateFreWindow.showAsDropDown(mUpdateSetBtn);
                AlertDialog.Builder mUpdatebuilder = new AlertDialog.Builder(this);

                final String[] freArray  = {"2小时","4小时","8小时","关闭自动更新"};
                mUpdatebuilder.setItems(freArray, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                mUpdateFrequency.setText("2小时");
                                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,2);
                                mUpdateServiceIntent.putExtra(Constants.INTENT_KEY_UPDATE_FRE,2);
                                startService(mUpdateServiceIntent);
                                break;
                            case 1:
                                mUpdateFrequency.setText("4小时");
                                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,4);
                                mUpdateServiceIntent.putExtra(Constants.INTENT_KEY_UPDATE_FRE,4);
                                startService(mUpdateServiceIntent);
                                break;
                            case 2:
                                mUpdateFrequency.setText("8小时");
                                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,8);
                                mUpdateServiceIntent.putExtra(Constants.INTENT_KEY_UPDATE_FRE,8);
                                startService(mUpdateServiceIntent);
                                break;
                            case 3:
                                mUpdateFrequency.setText("关闭");
                                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,0);
                                stopService(mUpdateServiceIntent);
                                break;
                        }
                    }
                });
                mUpdatebuilder.create();
                mUpdatebuilder.show();
                break;

//            case R.id.update_2h:
//                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,2);
//                mUpdateFreWindow.dismiss();
//                mUpdateFrequency.setText("2小时");
//                break;
//            case R.id.update_4h:
//                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,4);
//                mUpdateFreWindow.dismiss();
//                mUpdateFrequency.setText("4小时");
//                break;
//            case R.id.update_8h:
//                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,5);
//                mUpdateFreWindow.dismiss();
//                mUpdateFrequency.setText("8小时");
//                break;
//            case R.id.update_off:
//                PreferenceUtil.putLong(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.AUTO_UPDATE_FRE,0);
//                mUpdateFreWindow.dismiss();
//                mUpdateFrequency.setText("关闭");
//                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch(buttonView.getId()){
            case R.id.activity_setting_switch_location:
                PreferenceUtil.putBoolean(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.IS_LOCATING_ON,isChecked);
                break;

            case  R.id.activity_setting_switch_notification:
                PreferenceUtil.putBoolean(getApplicationContext(), Constants.Preferences.PREF_NAME, Constants.Preferences.IS_NOTI_SHOW,isChecked);
                if(isChecked){
                    String defCity = PreferenceUtil.getString(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.DEFAULT_CITY_ID,null);

                    Intent i = new Intent(getApplicationContext(), NotificationService.class);
                    if(null != defCity){
                        i.putExtra(Constants.INTENT_KEY_CITY_ID,defCity);
                    }
                    startService(i);

                }else {
                    stopService(new Intent(getApplicationContext(),NotificationService.class));

                }
                break;
        }
    }

}
