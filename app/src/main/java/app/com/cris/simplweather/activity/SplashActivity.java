package app.com.cris.simplweather.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import app.com.cris.simplweather.MainInfoActivity;
import app.com.cris.simplweather.R;
import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.model.CityEntity;
import app.com.cris.simplweather.presenter.SplashPresenter;
import app.com.cris.simplweather.service.UpdateService;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.utils.PreferenceUtil;
import app.com.cris.simplweather.viewinterface.SplashView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Cris on 2017/6/21.
 */

public class SplashActivity extends AppCompatActivity implements SplashView{

    private final int PERMISSION_REQUEST = 999;
    private SplashPresenter mPresenter;
    Unbinder mUnbinder;

    @BindView(R.id.iv_splash_icon)
    ImageView mSplashIcon;

    private boolean isAllPermissionGranted;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        isAllPermissionGranted = PreferenceUtil.getboolean(this.getApplicationContext(), Constants.Preferences.PREF_NAME,Constants.Preferences.IS_PERMISSION_GRANTED,false);
        mUnbinder = ButterKnife.bind(this);
        mPresenter = new SplashPresenter();
        mPresenter.attachView(this);
        if ( !isAllPermissionGranted  && Build.VERSION.SDK_INT >= 23){
            checkPermissions(Constants.PERMISSIONS);
        }else {
            mPresenter.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimation();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions(String...permissions){
        List<String> unGrantedPermission = new ArrayList<>();
        for (final String permision:permissions){
            if (checkSelfPermission(permision) != PackageManager.PERMISSION_GRANTED){

                if (shouldShowRequestPermissionRationale(permision)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    if (permision.equals(Constants.PERMISSIONS[0]) ){
                        builder.setMessage("需要您的授权，用来自动获取天气信息,请在即将弹出的对话框中点击“允许”");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{permision},PERMISSION_REQUEST);
                            }
                        });
                        builder.show();
                    }else if (permision.equals(Constants.PERMISSIONS[2]) ){
                        builder.setMessage("需要您的授权，以便存放天气信息,请在即将弹出的对话框中点击“允许”");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{permision},PERMISSION_REQUEST);
                            }
                        });
                        builder.show();
                    }else if (permision.equals(Constants.PERMISSIONS[4]) ) {
                        builder.setMessage("需要您的授权， 以便确定位置时使用， 请在即将弹出的对话框中点击“允许”");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{permision},PERMISSION_REQUEST);
                            }
                        });
                        builder.show();
                    }
                }
                else {
                    unGrantedPermission.add(permision);
                }
            }
        }
        if (null != unGrantedPermission && unGrantedPermission.size() > 0 ){

            requestPermissions(unGrantedPermission.toArray(new String[unGrantedPermission.size()]), PERMISSION_REQUEST);
        }
    }

    private void showMissingPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请前往设置界面开启权限，否则将退出应用");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private boolean verifyPermissions(int[] grantResults){
        for (int result: grantResults){
            if(result != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                    if (!verifyPermissions(grantResults)) {
                        showMissingPermissionDialog();
                        PreferenceUtil.putBoolean(getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.IS_PERMISSION_GRANTED,false);
                    }
                    else {
                        PreferenceUtil.putBoolean(getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.IS_PERMISSION_GRANTED,true);
                    }
                mPresenter.start();
                }
    }


    private void startAnimation() {
        mSplashIcon.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mSplashIcon, "translationY", 0, -(mSplashIcon.getHeight()/2 ));
                animator.setDuration(500);
                animator.setRepeatCount(ObjectAnimator.INFINITE);
                animator.setRepeatMode(ObjectAnimator.RESTART);
                animator.setInterpolator(new CycleInterpolator(0.5f));
                animator.start();
            }
        });
    }

    private void stopAnimation(){
        mSplashIcon.clearAnimation();
    }

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    public void navigationToCityWeatherActivty(CityEntity cityEntity) {

        Intent intent = new Intent(SplashActivity.this, WeatherPagerActivity.class);

        if(null != cityEntity){

            intent.putExtra(Constants.INTENT_KEY_STREET_NAME,cityEntity.getStreetName());
            intent.putExtra(Constants.INTENT_KEY_CITY_ID,cityEntity.getCityId());

        }
        startActivity(intent);

        int fre =(int) PreferenceUtil.getLong(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.AUTO_UPDATE_FRE,2);
        if(fre != 0){
            Intent i = new Intent(getApplicationContext(), UpdateService.class);
            i.putExtra(Constants.INTENT_KEY_UPDATE_FRE,fre);
            startService(i);
        }

        finish();
    }

    @Override
    public void navigationToCityPickActivity() {

        startActivity(new Intent(SplashActivity.this, CityPickActivity.class));

        int fre =(int) PreferenceUtil.getLong(getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.AUTO_UPDATE_FRE,2);
        if(fre != 0){
            Intent i = new Intent(getApplicationContext(), UpdateService.class);
            i.putExtra(Constants.INTENT_KEY_UPDATE_FRE,fre);
            startService(i);
        }

        finish();
    }

    @Override
    public void toastMessage(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

}

