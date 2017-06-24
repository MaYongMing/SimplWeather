package app.com.cris.simplweather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Cris on 2017/6/22.
 */

public class NetUtil {
    public static boolean isNetworkAvailable(Context context) {
        if (null == context)
            throw new IllegalArgumentException("isNetworkAvailable argument cannot be null");

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }

        return false;
    }
}
