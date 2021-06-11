package ch.zhaw.ch.gui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ch.zhaw.ch.MusicService;
import ch.zhaw.ch.R;

/***
 * Proviedes a View to set base settings for the app.
 */
public class SettingsView extends AppCompatActivity {
    private static final String TAG = SettingsView.class.getSimpleName();

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "Start up");
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            Log.v(TAG, playIntent.toString());
            Log.v(TAG, "Bind MusicService");
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            Log.v(TAG, Boolean.toString(musicBound));
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        musicSrv = null;
        super.onDestroy();
    }

    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "Bind Service");
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            Log.v(TAG, "get Service");
            musicSrv = binder.getService();
            //pass list

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void startPlayerView() {
        Log.v(TAG, "starting playerview");
        Intent intent = new Intent(this, PlayerView.class);
        startActivity(intent);
    }

    public void openList(View view) {
        Log.v(TAG, "open List");
        startListView();
    }

    public void openPlayer(View view) {
        Log.v(TAG, "open PLayer");
        //if(musicSrv.playingSong())
        // startPlayerView();
    }

    public void openSettings(View view) {
        Log.v(TAG, "open Settings");
    }

    private void startListView() {
        Intent intent = new Intent(this, SongListView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}