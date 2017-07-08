package app.com.cris.simplweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.adapter.CityManagerAdapter;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.presenter.CityManagerPresenter;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import app.com.cris.simplweather.viewinterface.CityManagerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cris on 2017/6/27.
 */

public class CityManagerActivity extends AppCompatActivity implements CityManagerView,View.OnClickListener{
    @BindView(R.id.activity_city_manager_list)
    RecyclerView mChosenCityRecyclerView;
    @BindView(R.id.activity_city_manager_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_city_manager_add)
    FloatingActionButton mAddButton;

    @BindView(R.id.activity_city_manager_cancel)
    Button mCancelSelectBtn;
    @BindView(R.id.activity_city_manager_commit)
    Button mCommitSelectBtn;

    @BindView(R.id.activity_city_manager_title)
    RelativeLayout mSelectCityTitle;

    private CityManagerAdapter mManagerAdapter;
    private CityManagerPresenter mPresenter;
    private boolean isCityListChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        initView();
        mPresenter = new CityManagerPresenter();
        mPresenter.attachView(this);
        mPresenter.start();
    }

    private void initView(){
        mChosenCityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddButton.setOnClickListener(this);
        mCancelSelectBtn.setOnClickListener(this);
        mCommitSelectBtn.setOnClickListener(this);

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showContent(List<WeatherEntity> list) {

        if(null == mManagerAdapter){
            mManagerAdapter = new CityManagerAdapter(this, list);
            mChosenCityRecyclerView.setAdapter(mManagerAdapter);
        }else {

            mManagerAdapter.resetCheckCount();
            mManagerAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_modify_city,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_action_modify:
                mManagerAdapter.showCheckBox();
                mManagerAdapter.resetCheckCount();
                mSelectCityTitle.setVisibility(View.VISIBLE);


                break;
            case android.R.id.home:

                if(isCityListChanged){
                    Intent intent = new Intent(this, WeatherPagerActivity.class);
                    intent.putExtra(Constants.INTENT_KEY_CITIES_CHANGED, true);
                    startActivity(intent);
                }

                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_city_manager_add:

                Intent intent = new Intent(this,CityPickActivity.class);
                startActivity(intent);
                break;

            case R.id.activity_city_manager_cancel:
                mManagerAdapter.hideCheckBox();
                mSelectCityTitle.setVisibility(View.GONE);

                break;

            case R.id.activity_city_manager_commit:
                mSelectCityTitle.setVisibility(View.GONE);
                List<String> checkedCityIds = mManagerAdapter.getCheckedCityId();
                if(checkedCityIds.size() >0 ){
                    isCityListChanged = true;
                }
                mManagerAdapter.hideCheckBox();
                mPresenter.removeChosenCities(checkedCityIds);
                mPresenter.start();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if(isCityListChanged){
            Intent intent = new Intent(this, WeatherPagerActivity.class);
            intent.putExtra(Constants.INTENT_KEY_CITIES_CHANGED, true);
            startActivity(intent);
        }
        else {
            super.onBackPressed();
        }

    }
}
