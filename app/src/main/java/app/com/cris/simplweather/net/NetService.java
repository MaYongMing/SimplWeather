package app.com.cris.simplweather.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Cris on 2017/6/22.
 */

public interface NetService {

    @GET
    Observable<ResponseBody> getCityList(@Url String url);
}
