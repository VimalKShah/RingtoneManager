package com.example.gunjan.ringtonemanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.support.design.widget.Snackbar;


public class MainActivity extends Activity implements View.OnClickListener {

    LinearLayout mainView;
    View main;
    RelativeLayout permission;
    List<Song> audioList = new ArrayList<>();

    MediaPlayer mMediaPlayer;
    String day;

    ImageButton mPlayAndStopRingtone;
    String path;
    String songName;
    boolean permission1;
    String ringtone;
    String id;
    Map<String,String> namemap =new HashMap<>();
    String defaultSongName;


    private PendingIntent pendingIntent;
    private AlarmManager manager;
    final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences1;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences1 = getSharedPreferences("Default Ringtone",
                Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("Ringtone Manager",
                Context.MODE_PRIVATE);

        initViews();





        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.i("time set is",""+calendar.getTime());

        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent0);







    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
            case 101:
                Log.d("Permission ", "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    permission.setVisibility(View.GONE);
                    mainView.setVisibility(View.VISIBLE);
                    actualCode();
                    //resume tasks needing this permission
                }
                break;
        }
    }

    public void actualCode() {
        //  listRingtones();



//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
//        registerReceiver(new DateTimeChangeReceiver(), intentFilter);

        Spinner spinner_days = (Spinner) findViewById(R.id.spinner13);
        List<String> list = new LinkedList<>(Arrays.asList(days));
        Log.d("list ", list.get(0));
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_days.setAdapter(adapter1);

        String fileList[];

        Spinner spinner = (Spinner) findViewById(R.id.spinner14);
        AssetManager assetManager = getAssets();

        // Memory song code


        //  String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME};// Can include more data for more details and check it.
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null, null);

        path = MediaStore.Audio.Media.DATA;
        Log.i("Path is ", path);
        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    Log.e("id", audioCursor.getString(0));
                    id=audioCursor.getString(0);
                    Log.e("data no internal",""+MediaStore.Audio.Media.getContentUriForPath(audioCursor.getString(data)));
                    namemap.put(id,audioCursor.getString(audioIndex));
                    audioList.add(new Song(audioCursor.getString(audioIndex), audioCursor.getString(data),id,MediaStore.Audio.Media.getContentUriForPath(audioCursor.getString(data))));

                    Log.i("audioCursor", audioCursor.getString(data));
                    //  audioList.add("Sunday");

                } while (audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        Cursor audioCursorexternal = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        path = MediaStore.Audio.Media.DATA;
        Log.i("Path is ", path);
        if (audioCursorexternal != null) {
            if (audioCursorexternal.moveToFirst()) {
                do {
                    int audioIndex = audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int data = audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    id=audioCursorexternal.getString(0);
                    Log.e("id", audioCursorexternal.getString(0));
                    Log.e("data no",""+audioCursorexternal.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    audioList.add(new Song(audioCursorexternal.getString(audioIndex), audioCursorexternal.getString(data),id,MediaStore.Audio.Media.getContentUriForPath(audioCursorexternal.getString(data))));

                    Log.i("audioCursor", audioCursorexternal.getString(data));
                    //  audioList.add("Sunday");

                } while (audioCursorexternal.moveToNext());
            }
        }
        audioCursorexternal.close();
        //listRingtones();

        List<String> displayName = new ArrayList<>();
        for (Song list11 : audioList) {
            displayName.add(list11.getSongName());
        }
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,displayName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        setDefaultRingtone();


//        SharedPreferences sharedPreferences2 = getSharedPreferences("Ringtone Manager",
//                Context.MODE_PRIVATE);
//        String savedSongName=sharedPreferences2.getString("Sunday","");
//        Log.i("testing",savedSongName);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                songName = parent.getItemAtPosition(position).toString();
                Log.i("Text ", songName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = parent.getItemAtPosition(position).toString();
                Log.i("Selected day is ", day);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && Settings.System.canWrite(this)){
            Log.d("TAG", "CODE_WRITE_SETTINGS_PERMISSION success");
            checkPermissions();
        }
    }

    private void setDefaultRingtone()
    {
        Uri ringtone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE);

        Log.e("Default Ringtone is" , ""+ringtone);
        Log.i("haha","haha");
        for (Song song : audioList) {
            Log.i("song.getStoragePath()",song.getStoragePath().toString());
            Log.i("ringtone",ringtone.toString());

            if (ContentUris.withAppendedId(song.getStoragePath(), Long.valueOf(song.getId())).equals(ringtone)) {
                defaultSongName=song.getSongName();
                break;
            }
        }
        if((sharedPreferences1.getString("defaultRingtone","").equals(""))) {
            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
            editor1.putString("defaultRingtone", defaultSongName);
            editor1.commit();
            for (String day : days) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(day, defaultSongName);
                editor.apply();
            }

        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play_and_stop_ringtone:
                playAndStopRingtone();
                break;
            case R.id.set_ringtone:
                setRingtone();
                break;
            case R.id.reset_ringtone:
                resetRingtone();
                break;
        }
    }

    private void initViews(){
        mainView = (LinearLayout) findViewById(R.id.mainView);
        main = findViewById(R.id.mainLayout);
        permission = (RelativeLayout) findViewById(R.id.permissionView);

        mPlayAndStopRingtone = (ImageButton) findViewById(R.id.play_and_stop_ringtone);
        ImageButton setRingtone = (ImageButton) findViewById(R.id.set_ringtone);
        ImageButton resetRingtone = (ImageButton) findViewById(R.id.reset_ringtone);

        mPlayAndStopRingtone.setOnClickListener(this);
        setRingtone.setOnClickListener(this);
        resetRingtone.setOnClickListener(this);

        showSystemPermissionView();
    }

    private void playAndStopRingtone() {
        try {
            String path;
            if (mMediaPlayer != null &&mMediaPlayer.isPlaying()) {
                mPlayAndStopRingtone.setImageDrawable(getResources().getDrawable(R.drawable.play_32));
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer = null;

            } else {
                for (Song song : audioList) {
                    if (song.getSongName().equals(songName)) {
                        mPlayAndStopRingtone.setImageDrawable(getResources().getDrawable(R.drawable.stop_32));
                        path = song.getPath();
                        if(mMediaPlayer == null) {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setDataSource(path);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                        }
                    }
                }
            }

            if(mMediaPlayer != null) {
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayAndStopRingtone.setImageDrawable(getResources().getDrawable(R.drawable.play_32));
                        mp.stop();
                        mp.reset();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRingtone() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(day,songName);
        editor.apply();
        Snackbar.make(main, "Ringtone Set for "+day+ " is "+sharedPreferences.getString(day,""), Snackbar.LENGTH_LONG).show();
    }

    private void resetRingtone() {
        SharedPreferences sharedPreferences4 = getSharedPreferences("Default Ringtone",
                Context.MODE_PRIVATE);
        String defaults=sharedPreferences4.getString("defaultRingtone","");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(day, defaults);
        editor.apply();
        changeRingtone(audioList);
    }

    private void changeRingtone(List<Song> audioList1) {

        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
        SharedPreferences sharedPreferences3 = getSharedPreferences("Ringtone Manager",
                Context.MODE_PRIVATE);
        String savedSongName=sharedPreferences3.getString(simpleDateformat.format(now),"");

        for (Song song : audioList1) {
            if (song.getSongName().equals(savedSongName)) {
                Uri uri = ContentUris.withAppendedId(song.getStoragePath(), Long.valueOf(song.getId()));
                RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, uri);

            }
        }
        Snackbar.make(main, "Ringtone Set for "+day+ " is "+ savedSongName, Snackbar.LENGTH_LONG).show();

    }

    private void showSystemPermissionView() {
        boolean writeSettingsPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            writeSettingsPermission = Settings.System.canWrite(this);
        } else {
            writeSettingsPermission = ContextCompat.checkSelfPermission
                    (this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (writeSettingsPermission) {
            checkPermissions();
        }  else {
            //main.setAlpha(0);
            Snackbar snackbar = Snackbar
                    .make(main, "Please provide the system permission",  Snackbar.LENGTH_INDEFINITE)
                    .setAction("Click me", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                Intent intent1 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent1.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                                MainActivity.this.startActivityForResult(intent1, 101);
                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, 101);
                            }
                        }
                    });

            snackbar.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission", "Permission is granted1");
                permission.setVisibility(View.GONE);
                mainView.setVisibility(View.VISIBLE);
                actualCode();
            } else {
                Log.v("Permission", "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
    }
}