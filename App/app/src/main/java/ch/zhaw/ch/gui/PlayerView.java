package ch.zhaw.ch.gui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;

import java.util.stream.Collectors;

import ch.zhaw.ch.Config;
import ch.zhaw.ch.ConfigHandler;
import ch.zhaw.ch.MusicService;
import ch.zhaw.ch.R;
import ch.zhaw.ch.dsp.phase.TransientDetectionType;
import ch.zhaw.ch.dsp.phase.PhaseResetType;
import ch.zhaw.ch.dsp.phase.PhaseShifterModule;

/***
 * A View that provides music player functionalities and lets the user set and alter the behaviour of the vocoders.
 */

public class PlayerView extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = PlayerView.class.getSimpleName();
    private Config config;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    GraphView graph;
    private int maxPitchShiftFactor = 24;
    private int middle;
    private int confHalfToneStepsToShift = 0;
    private int confPhaseShiftModuleIndex = 0;
    private int confTransientDetectionIndex = 0;
    private int confPhaseResetTypeIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = Config.getInstance();
        setContentView(R.layout.activity_player_view);
        //graph = (GraphView) findViewById(R.id.graph);

        confPhaseShiftModuleIndex = PhaseShifterModule.getValue(config.getModule().getPhaseShifter()).getIndex();
        confHalfToneStepsToShift = middle + config.getTransform().getHalfToneStepsToShift();

        Spinner transientDetectionDropDown = findViewById(R.id.transientDetectionType);
        ArrayAdapter<String> transientDetectionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                TransientDetectionType.getValues().stream().map(p -> p.getDisplayName()).collect(Collectors.toList())
        );

        Log.v(TAG, ""+confPhaseShiftModuleIndex);
        transientDetectionDropDown.setAdapter(transientDetectionAdapter);
        transientDetectionDropDown.setOnItemSelectedListener(this);
        transientDetectionDropDown.setSelection(TransientDetectionType.getValue(config.getModule().getTransientDetector()).getIndex());


        Spinner phaseResetDropDown = findViewById(R.id.phaseResetType);
        ArrayAdapter<String> phaseResetAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                PhaseResetType.getValues().stream().map(p -> p.getDisplayName()).collect(Collectors.toList())
        );

        phaseResetDropDown.setAdapter(phaseResetAdapter);
        phaseResetDropDown.setOnItemSelectedListener(this);
        phaseResetDropDown.setSelection(PhaseResetType.getValue(config.getModule().getPhaseResetType()).getIndex());

        Spinner phaseShiftDropDown = findViewById(R.id.phaseShiftAlgorithm);
        ArrayAdapter<String> phaseShiftAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                PhaseShifterModule.getValues().stream().map(p -> p.getDisplayName()).collect(Collectors.toList())
        );

        phaseShiftDropDown.setAdapter(phaseShiftAdapter);
        phaseShiftDropDown.setOnItemSelectedListener(this);
        phaseShiftDropDown.setSelection(PhaseShifterModule.getValue(config.getModule().getPhaseShifter()).getIndex());

        SeekBar halftoneStepsToShiftBar = findViewById(R.id.seekBar);

        TextView pitchShiftFactorPercent = findViewById(R.id.pitchShiftFactorPercent);

        middle = (int) Math.ceil(maxPitchShiftFactor / 2);
        pitchShiftFactorPercent.setText("" + config.getTransform().getHalfToneStepsToShift());
        halftoneStepsToShiftBar.setMax(maxPitchShiftFactor);
        halftoneStepsToShiftBar.setProgress(middle + config.getTransform().getHalfToneStepsToShift());
        halftoneStepsToShiftBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TextView pitchShiftFactorPercent = findViewById(R.id.pitchShiftFactorPercent);
                //progress
                confHalfToneStepsToShift = (progress - middle);
                pitchShiftFactorPercent.setText("" + confHalfToneStepsToShift);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    /**
     * sets up the connection to the MusicService.
     */
    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "Bind Service");
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            Log.v(TAG, "get Service");
            musicService = binder.getService();
            //pass list

            musicBound = true;
            setSongDetails(musicService.getCurrentSong());
            musicService.setGraph(graph);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void setSongDetails(SongInfo currentSong) {
        TextView title = (TextView) findViewById(R.id.songTitle);
        TextView artist = (TextView) findViewById(R.id.songArtist);

        if (currentSong != null) {
            title.setText(currentSong.getTitle());
            artist.setText(currentSong.getArtist());
        } else {
            title.setText("undefined");
            artist.setText("undefined");
        }
    }

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

    @Override
    protected void onDestroy() {
        musicService.unsetGraph();
        unbindService(musicConnection);
        musicService = null;
        super.onDestroy();
    }

    public void togglePlaySong(View view) {
        Log.v(TAG, "play or pause current song");
        musicService.togglePlaySong();
    }

    public void restartSong(View view){
        musicService.restartSong();
    }

    public void applySettings(View view) {
        Config config = Config.getInstance();
        Log.v(TAG, "apply Settings ShiftFactor " + confHalfToneStepsToShift);
        config.setHalfToneStepsToShift(confHalfToneStepsToShift);
        config.setPitchShiftAlgorithm(confPhaseShiftModuleIndex);
        config.setTransientDetectionType(confTransientDetectionIndex);
        config.setPhaseResetType(confPhaseResetTypeIndex);



        musicService.applySettings();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.v(TAG, ""+parent.getId()+" "+id);

        if(parent.getId() == R.id.phaseShiftAlgorithm) {
            confPhaseShiftModuleIndex = position;
        }

        if(parent.getId() == R.id.transientDetectionType) {
            confTransientDetectionIndex = position;
        }

        if(parent.getId() == R.id.phaseResetType) {
            confPhaseResetTypeIndex = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Log.v(TAG, "noting");
    }
}