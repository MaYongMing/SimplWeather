package app.com.cris.simplweather.net;



import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import app.com.cris.simplweather.db.CityListDatabase;
import app.com.cris.simplweather.model.CityEntity;
import app.com.cris.simplweather.utils.Constants;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Cris on 2017/6/22.
 */

public class DownManager {

    private static DownManager mManager;
    private NetService mNetService;

    private DownManager(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://free-api.heweather.com/v5/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mNetService= retrofit.create(NetService.class);

    }

    public static synchronized DownManager getInstance(){
        if(mManager == null){
            return new DownManager();
        }
        else {
            return mManager;
        }
    }

    public Observable<List<CityEntity>> getCityList(){

       return mNetService.getCityList(Constants.CITY_LIST_URL)
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<ResponseBody, ObservableSource<? extends List<CityEntity>>>() {
                    @Override
                    public ObservableSource<? extends List<CityEntity>> apply(@NonNull final ResponseBody responseBody) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<List<CityEntity>>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<List<CityEntity>> e) throws Exception {
                                List<CityEntity>  cityList = new ArrayList<>();
                                BufferedReader buffer = new BufferedReader(new InputStreamReader(responseBody.byteStream()));
                                String line = "";
                                int index = 0;
                                while ((line = buffer.readLine()) != null){
                                    if(index > 2){
                                        String[] cityInfo = line.split("\\t");
                                        CityEntity cityEntity = new CityEntity();
                                        cityEntity.setCityId( cityInfo[0]);
                                        cityEntity.setDistrictEngName(cityInfo[1]);
                                        cityEntity.setDistrictName(cityInfo[2]);
                                        cityEntity.setCityName(cityInfo[9]);
                                        cityEntity.setProvinceName(cityInfo[7]);

                                        cityList.add(cityEntity);
                                    }
                                    index++;
                                }
                                e.onNext(cityList);
                            }
                        });
                    }
                });
    }
}
