package com.example.multimedia;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> displayList = new ArrayList<>();
        displayList.add("mediaplayer");
        displayList.add("mediacodec+mediaextractor");
        displayList.add("exoplayer");
        displayList.add("TBLPlayer");
        displayList.add("GLSurfaceView");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, displayList);
        setListAdapter(adapter);
        checkPermission();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (position == 0) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MediaPlayerActivity.class);
            startActivity(intent);
        } else if (position == 1) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MediaCodecMediaExtractorActivity.class);
            startActivity(intent);
        } else if (position == 2) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExoplayerActivity.class);
            startActivity(intent);
        } else if (position == 3) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, GLSurfaceViewActivity.class);
            startActivity(intent);
        } else if (position == 4) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExoplayerActivity.class);
            startActivity(intent);
        } else if (position == 5) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExoplayerActivity.class);
            startActivity(intent);
        } else if (position == 6) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExoplayerActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return false;
        }
        return true;
    }
}