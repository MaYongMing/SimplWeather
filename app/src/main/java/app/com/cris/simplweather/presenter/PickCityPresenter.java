package app.com.cris.simplweather.presenter;

import java.util.ArrayList;
import java.util.List;

import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.model.CityEntity;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.PreferenceUtil;
import app.com.cris.simplweather.viewinterface.BaseView;
import app.com.cris.simplweather.viewinterface.CityPickView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Cris on 2017/6/29.
 */

public class PickCityPresenter extends BasePresenter {

    private CityPickView mPickCityView;
    private Observable<List<CityEntity>> mLoadListObservable;
    private Disposable mDisposable;
    private List<CityEntity> mCityEntityList;

    public PickCityPresenter(){
        mLoadListObservable = Observable.create(new ObservableOnSubscribe<List<CityEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CityEntity>> e) throws Exception {
                List<CityEntity> cityEntities = CityListDatabase.getInstance(mPickCityView.getContext()).loadAllData();
                e.onNext(cityEntities);
            }
        }).subscribeOn(Schedulers.io());

    }
    @Override
    public void attachView(BaseView baseView) {
        mPickCityView = (CityPickView) baseView;
    }

    @Override
    public void start() {
        mDisposable =   mLoadListObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CityEntity>>() {
                    @Override
                    public void accept(@NonNull List<CityEntity> list) throws Exception {
                        if(null != list){
                            mCityEntityList = list;
                            mPickCityView.deliveryCityList(list);

                        }
                        else {
                            mPickCityView.toastMessage("读取城市列表失败，请重新启动程序");
                            PreferenceUtil.putBoolean(mPickCityView.getContext().getApplicationContext(),Constants.Preferences.PREF_NAME,Constants.Preferences.IS_DB_CREATED,false);
                        }
                    }
                });
    }

    public void refreshCityList(String input){
        List<CityEntity> alternativeCities = new ArrayList<>();
        for (CityEntity city: mCityEntityList){
            if(city.getProvinceName().contains(input) || city.getCityName().contains(input) || city.getDistrictName().contains(input) ){
                alternativeCities.add(city);
            }
        }
        mPickCityView.deliveryCityList(alternativeCities);
    }


    @Override
    public  void stop() {
        if (null != mDisposable && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }
}
