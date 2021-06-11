package ch.zhaw.ch.gui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ch.zhaw.ch.R;

/**
 * author lopezale
 * First View to be executed when starting the app. Asks permissions for the app if necessary and continues to the SongList View.
 */

public class SplashScreen extends AppCompatActivity {
    static final String TAG = SplashScreen.class.getSimpleName();
    static final Integer READ_EXST = 0x1;
    static final String externalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.v(TAG, "check if permission already granted");
        if (checkIfPermissionGranted(externalStoragePermission)) {
            startListView();
        } else {
            Log.v(TAG, "Ask for permision to ext storage");
            ActivityCompat.requestPermissions(this, new String[]{externalStoragePermission}, READ_EXST);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXST) {
            if (ContextCompat.checkSelfPermission(this, externalStoragePermission) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission request granted");
                startListView();
            } else {
                Log.v(TAG, "Permission request not granted");
                this.finishAffinity();
            }
        }
    }

    private boolean checkIfPermissionGranted(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void startListView() {
        Intent intent = new Intent(this, SongListView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}