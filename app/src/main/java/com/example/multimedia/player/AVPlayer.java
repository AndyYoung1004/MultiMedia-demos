package com.example.multimedia.player;

import android.view.Surface;

public class AVPlayer implements IMediaPlayer {
    IExtractor audioExtractor;
    IExtractor videoExtractor;
    DecoderBase audioDecoder;
    DecoderBase videoDecoder;
    Surface surface;
    AVPlayer() {
        audioExtractor = new AudioExtractor();
        videoExtractor = new VideoExtractor();
        audioDecoder = new AudioDecoder();
        videoDecoder = new VideoDecoder();
    }

    @Override
    public void setDataSource(String filePath) {
        audioExtractor.setDataSource(filePath);
        videoExtractor.setDataSource(filePath);
    }

    @Override
    public void setDisplay(Surface surface) {
        this.surface = surface;
    }

    @Override
    public void prepare() {
        audioDecoder.configure(audioExtractor.getTrackFormat(), null, null, 0);
        videoDecoder.configure(videoExtractor.getTrackFormat(), surface, null, 0);
    }

    @Override
    public void start() {
        startLoaderThread();
        startRenderThread();
    }

    @Override
    public void stop() {

    }

    void startLoaderThread() {

    }

    void startRenderThread() {

    }
}
