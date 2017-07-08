package app.com.cris.simplweather.net;

import java.util.List;
import java.util.Map;

import app.com.cris.simplweather.model.CityWeatherResponse;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Cris on 2017/6/22.
 */

public interface NetService {

    @GET
    Observable<ResponseBody> getCityList(@Url String url);

    //https://free-api.heweather.com/v5/weather?city=CN101043500&key=5597b983af944721a3f2dae92b4ccd4d
    @GET("v5/weather")
    Observable<CityWeatherResponse> getWeatherResponse(@QueryMap Map<String, String> params);
}
