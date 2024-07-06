package com.example.multimedia.player;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

public interface IExtractor {
    void setDataSource(String input);
    void setFileType(String type);
    MediaFormat init();
    long getSampleTime();
    boolean advance();
    int readSampleData(ByteBuffer inputBuffer);
}
