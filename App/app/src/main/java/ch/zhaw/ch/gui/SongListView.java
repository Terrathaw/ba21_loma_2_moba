package ch.zhaw.ch.gui;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.zhaw.ch.Config;
import ch.zhaw.ch.MusicService;
import ch.zhaw.ch.MusicService.MusicBinder;
import ch.zhaw.ch.R;

/**
 * @author: lopezale
 * Song list View of this application. It displays all available songs and adds some functionality for sending songs to the MusicService.
 */
public class SongListView extends AppCompatActivity {

    private static final String TAG = SongListView.class.getSimpleName();

    private ArrayList<SongInfo> songInfoList;
    private ListView songListView;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        String versionNumber = Config.getInstance().getVersionNumber();
        TextView version = findViewById(R.id.version);
        version.setText("Version: "+versionNumber);

        Log.v(TAG, "Get Songs");
        songInfoList = new ArrayList<>();
        songListView = findViewById(R.id.songsList);

        songInfoList.add(new SongInfo(Uri.parse("android.resource://ch.zhaw.ch/" + R.raw.midi_melodic), "Midi Melodic", "Thesis"));
        songInfoList.add(new SongInfo(Uri.parse("android.resource://ch.zhaw.ch/" + R.raw.midi_percussive), "Midi Percussive", "Thesis"));
        songInfoList.add(new SongInfo(Uri.parse("android.resource://ch.zhaw.ch/" + R.raw.midi_mixed), "Midi Mixed", "Thesis"));

        retrieveSongs();
        sortSongs();

        Log.v(TAG, "create new Adapter");
        SongAdapter songAdapter = new SongAdapter(this, songInfoList);
        Log.v(TAG, "set Adapter to View");
        songListView.setAdapter(songAdapter);
    }

    /**
     * sets up the connection to the MusicService.
     */
    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "Bind Service");
            MusicBinder binder = (MusicBinder) service;
            //get service
            Log.v(TAG, "get Service");
            musicSrv = binder.getService();
            //pass list

            musicSrv.setList(songInfoList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        Log.v(TAG, "start up");
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

    public void retrieveSongs() {
        retrieveSongWithUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        retrieveSongWithUri(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
    }

    private void retrieveSongWithUri(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = null;
        try {

            cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int mimeTypeCol = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
                int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                do {
                    if (!"audio/x-wav".equals(cursor.getString(mimeTypeCol))) continue;
                    Log.v(TAG, "Add song to list: " + songTitle);
                    Uri songUri = ContentUris.withAppendedId(uri, cursor.getLong(songId));
                    songInfoList.add(new SongInfo(songUri, cursor.getString(songTitle), cursor.getString(songArtist)));

                    Log.v("musicUri: ", Long.toString(cursor.getLong(songId)));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.getStackTraceString(e);
        } finally {
            cursor.close();
        }
    }

    /**
     * Sorts Songs according to the Selected methode.
     */
    private void sortSongs() {
        Log.v(TAG, "Sort song list.");
        sortSongsWithTitle();
        for (SongInfo songInfo : songInfoList) {
            Log.v(TAG, songInfo.getTitle());
        }
    }

    private void sortSongsWithTitle() {
        Collections.sort(songInfoList, new Comparator<SongInfo>() {
            @Override
            public int compare(SongInfo o1, SongInfo o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
    }

    public void selectedSong(View view) {
        int songPosition = Integer.parseInt(view.getTag().toString());
        Log.v(TAG, Integer.toString(songPosition));
        musicSrv.playSong(songPosition);
        startPlayerView();
    }


    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        musicSrv = null;
        super.onDestroy();
    }

    private void startPlayerView() {
        Log.v(TAG, "starting playerview");
        Intent intent = new Intent(this, PlayerView.class);
        startActivity(intent);
    }

    public void openList(View view) {
        Log.v(TAG, "open List");
    }

    public void openPlayer(View view) {
        Log.v(TAG, "open PLayer");
        if (musicSrv.playingSong())
            startPlayerView();
    }

    public void openSettings(View view) {
        Log.v(TAG, "open Settings");
        startSettingsView();
    }

    private void startSettingsView() {
        Intent intent = new Intent(this, SettingsView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}