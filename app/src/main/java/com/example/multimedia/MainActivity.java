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
    private final int MEDIA_PLAYER_ACTIVITY = 0;
    private final int MEDIA_CODEC_ACTIVITY = 1;
    private final int EXO_PLAYER_ACTIVITY = 2;
    private final int TBL_PLAYER_ACTIVITY = 3;
    private final int GL_SFVIEW_ACTIVITY = 4;
    private final int GL_PICTURE_ACTIVITY = 5;
    private final int GL_CAMERA_PICTURE_ACTIVITY = 6;
    private final int GL_FBO_ACTIVITY = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> displayList = new ArrayList<>();
        displayList.add("mediaplayer播放视频");
        displayList.add("mediacodec+mediaextractor播放视频");
        displayList.add("exoplayer播放视频");
        displayList.add("TBLPlayer播放视频");
        displayList.add("GLSurfaceView播放视频");
        displayList.add("GLPicture播放图片");
        displayList.add("相机预览拍摄图片");
        displayList.add("照片FBO");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, displayList);
        setListAdapter(adapter);
        checkPermission();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (position == MEDIA_PLAYER_ACTIVITY) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MediaPlayerActivity.class);
            startActivity(intent);
        } else if (position == MEDIA_CODEC_ACTIVITY) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MediaCodecMediaExtractorActivity.class);
            startActivity(intent);
        } else if (position == EXO_PLAYER_ACTIVITY) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExoplayerActivity.class);
            startActivity(intent);
        } else if (position == TBL_PLAYER_ACTIVITY) {
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, GLSurfaceViewActivity.class);
//            startActivity(intent);
        } else if (position == GL_SFVIEW_ACTIVITY) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, GLSurfaceViewActivity.class);
            startActivity(intent);
        } else if (position == GL_PICTURE_ACTIVITY) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, GLPictureActivity.class);
            startActivity(intent);
        } else if (position == GL_CAMERA_PICTURE_ACTIVITY) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, GLCameraPictureActivity.class);
            startActivity(intent);
        } else if (position == GL_FBO_ACTIVITY) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, FBOActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 0);
            return false;
        }
        return true;
    }
}