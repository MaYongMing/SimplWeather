package app.com.cris.simplweather.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import app.com.cris.simplweather.R;
import app.com.cris.simplweather.adapter.CityListRecyclerViewAdapter;
import app.com.cris.simplweather.model.CityEntity;
import app.com.cris.simplweather.presenter.PickCityPresenter;
import app.com.cris.simplweather.viewinterface.CityPickView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Cris on 2017/6/23.
 */

public class CityPickActivity extends AppCompatActivity implements CityPickView,View.OnClickListener{



    @BindView(R.id.activity_pick_city_recycler)
    protected RecyclerView mCityListRecyclerView;
    @BindView(R.id.activity_pick_city_back)
    protected ImageButton mNavigationBackBtn;
    @BindView(R.id.activity_pick_city_clear)
    protected ImageButton mClearInputBtn;
    @BindView(R.id.activity_pick_city_input)
    protected EditText mEditText;



    private PickCityPresenter mPickCityPresenter;
    private CityListRecyclerViewAdapter mRecyclerAdapter;
    private List<CityEntity> mCityEntityList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);
        ButterKnife.bind(this);

        initView();

        mPickCityPresenter = new PickCityPresenter();
        mPickCityPresenter.attachView(this);
        mPickCityPresenter.start();

    }

    private void initView(){

        mNavigationBackBtn.setOnClickListener(this);
        mClearInputBtn.setOnClickListener(this);
        mCityEntityList = new ArrayList<>();
        mRecyclerAdapter = new CityListRecyclerViewAdapter(this,mCityEntityList);
        mCityListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCityListRecyclerView.setAdapter(mRecyclerAdapter);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mClearInputBtn.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            mPickCityPresenter.refreshCityList(mEditText.getText().toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPickCityPresenter.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mUnbinder.unbind();
    }


    @Override
    public void deliveryCityList(List<CityEntity> list) {
        mCityEntityList.clear();
        mCityEntityList.addAll(list);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void toastMessage(String msg) {
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_pick_city_back:
                finish();
                break;
            case R.id.activity_pick_city_clear:
                mEditText.setText(null);
                mClearInputBtn.setVisibility(View.GONE);
                break;
        }
    }
}
