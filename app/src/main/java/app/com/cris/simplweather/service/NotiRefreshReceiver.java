package app.com.cris.simplweather.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.com.cris.simplweather.utils.Constants;

/**
 * Created by Cris on 2017/7/9.
 */

public class NotiRefreshReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, NotificationService.class);
        context.startService(i);
    }
}
