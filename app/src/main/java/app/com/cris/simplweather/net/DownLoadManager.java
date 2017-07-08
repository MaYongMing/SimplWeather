package app.com.cris.simplweather.net;



import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.cris.simplweather.model.CityEntity;
import app.com.cris.simplweather.model.CityWeatherResponse;
import app.com.cris.simplweather.model.WeatherEntity;
import app.com.cris.simplweather.utils.Constants;
import app.com.cris.simplweather.utils.LogUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Cris on 2017/6/22.
 */

public class DownLoadManager {

    private static DownLoadManager mManager;
    private NetService mNetService;

    private DownLoadManager(){
 //       OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor()).build();
        Retrofit retrofit = new Retrofit.Builder()
 //               .client(client) ////Test
                .baseUrl("https://free-api.heweather.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mNetService= retrofit.create(NetService.class);

    }

//    //for debug
//    class LoggingInterceptor implements Interceptor {
//        @Override public Response intercept(Interceptor.Chain chain) throws IOException {
//
//            Request request = chain.request();
//            LogUtil.d(Constants.DEBUG_TAG, "url is "+request.url().toString());
//            Response response = chain.proceed(request);
//
//            return response;
//        }
//    }

    public static synchronized DownLoadManager getInstance(){
        if(mManager == null){
            return new DownLoadManager();
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


    public Observable<WeatherEntity> getWeatherResponse(String cityId){
        Map<String, String> params = new HashMap<>();
        params.put("city",cityId);
        params.put("key",Constants.KEY);
        return mNetService.getWeatherResponse(params).map(new Function<CityWeatherResponse, WeatherEntity>() {
            @Override
            public WeatherEntity apply(@NonNull CityWeatherResponse cityWeatherResponse) throws Exception {
                if (null == cityWeatherResponse) {
                    throw new DownLoadException("Can't get city weather response. ");
                }

                CityWeatherResponse.HeWeather5Bean weatherResponse =  cityWeatherResponse.getHeWeather5().get(0);

                if(null == weatherResponse ) {
                    throw new DownLoadException("Can't get city weather response. ");
                }
                if( !weatherResponse.getStatus().equals("ok")){
                    throw new DownLoadException("Invalid request, error code: " + weatherResponse.getStatus() );
                }

                WeatherEntity weatherEntity = new WeatherEntity();

                weatherEntity.setStatus(weatherResponse.getStatus());
                weatherEntity.setCityName(weatherResponse.getBasic().getCity());//城市名
                weatherEntity.setCityId(weatherResponse.getBasic().getId());//城市代码
                weatherEntity.setCurCond(weatherResponse.getNow().getCond().getTxt());//天气
                weatherEntity.setConCode(weatherResponse.getNow().getCond().getCode());//天气代码
                weatherEntity.setHumidity(weatherResponse.getNow().getHum());//湿度
                weatherEntity.setUpdateTime(weatherResponse.getBasic().getUpdate().getLoc());//发布时间
                weatherEntity.setCurTmp(weatherResponse.getNow().getTmp());//温度
                weatherEntity.setFeelTmp(weatherResponse.getNow().getFl());//体感温度
                if(null != weatherResponse.getAqi()) {//部分城市没有aqi数据
                    weatherEntity.setAqi(weatherResponse.getAqi().getCity().getAqi());//空气质量指数
                    weatherEntity.setAirQlty(weatherResponse.getAqi().getCity().getQlty());//空气质量
                    weatherEntity.setPm2p5(weatherResponse.getAqi().getCity().getPm25());//pm2.5指数
                }else {
                    weatherEntity.setAqi("0");//空气质量指数
                    weatherEntity.setAirQlty("未知");//空气质量
                    weatherEntity.setPm2p5("0");//pm2.5指数

                }
                weatherEntity.setWindDir(weatherResponse.getNow().getWind().getDir());//风向
                weatherEntity.setWindLvl(weatherResponse.getNow().getWind().getSc());//风力等级
                weatherEntity.setDailyForecasts(weatherResponse.getDaily_forecast());//未来一周
                weatherEntity.setSuggestion(weatherResponse.getSuggestion());//建议

                return weatherEntity;
            }
        });
    }
}
