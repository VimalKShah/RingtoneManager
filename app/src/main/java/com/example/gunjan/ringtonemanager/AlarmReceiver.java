package com.example.gunjan.ringtonemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gunjan on 20-05-18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Running or not", "Yes");
        Toast.makeText(context, "I'm running ", Toast.LENGTH_LONG).show();
        Intent intent1=new Intent(context,ActualCodeService.class);
        context.startForegroundService(intent1);
    }
}
