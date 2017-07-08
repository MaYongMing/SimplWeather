package app.com.cris.simplweather.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.com.cris.simplweather.utils.Constants;

/**
 * Created by Cris on 2017/7/7.
 */

public class WakeUpReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int flag = intent.getIntExtra(Constants.INTENT_KEY_UPDATE_FRE,8);
        Intent i = new Intent(context, UpdateService.class);
        i.putExtra(Constants.INTENT_KEY_UPDATE_FRE,flag);
        context.startService(i);

    }
}
