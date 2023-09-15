package com.example.multimedia.player;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

public interface IExtractor {
    void setDataSource(String filePath);
    void selectTrack(int trackIndex);
    void release();
    int readSampleData(ByteBuffer inputBuffer);
    long getSampleTime();
    boolean advance();
    MediaFormat getTrackFormat();
}
