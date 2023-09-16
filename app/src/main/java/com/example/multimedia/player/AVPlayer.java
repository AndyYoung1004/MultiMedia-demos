package com.example.multimedia.player;

import android.view.Surface;

public class AVPlayer implements IMediaPlayer {

    private MediaCodecAudioRenderer audioRenderer;
    private MediaCodecVideoRenderer videoRenderer;
    private String filePath;
    private Surface surface;

    @Override
    public void setDataSource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void setDisplay(Surface surface) {
        this.surface = surface;
    }

    @Override
    public void prepare() {
        audioRenderer = new MediaCodecAudioRenderer(filePath);
        videoRenderer = new MediaCodecVideoRenderer(filePath, surface);
        audioRenderer.initMediaCodecRenderer();
        audioRenderer.initAudioTrack();
        videoRenderer.initMediaCodecRenderer();
    }

    @Override
    public void start() {
        audioRenderer.start();
        videoRenderer.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    audioRenderer.renderFrame();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (videoRenderer.getTimeStamp() < audioRenderer.getTimeStamp()) {
                        videoRenderer.renderFrame();
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void stop() {

    }
}
