package com.example.multimedia.player;

import android.view.Surface;

public interface IMediaPlayer {
    void setDataSource(String filePath);
    void setDisplay(Surface surface);
    void prepare();
    void start();
    void stop();
}
