package com.example.multimedia.player;

import android.util.Log;
import android.view.Surface;

public class AVPlayer implements IMediaPlayer {
    private static final String TAG = "FinalShaderWrapper";
    private MediaCodecAudioRenderer audioRenderer;
    private MediaCodecVideoRenderer videoRenderer;
    private String filePath;
    private Surface surface;
    private boolean playerStarted;
    private boolean isAudioOver, isVideoOver;

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
        playerStarted = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                isAudioOver = false;
                while (playerStarted) {
                    audioRenderer.renderFrame();
                }
                isAudioOver = true;
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                isVideoOver = false;
                while (playerStarted) {
                    if (videoRenderer.getTimeStamp() < audioRenderer.getTimeStamp()) {
                        videoRenderer.renderFrame();
                    } else if (!playerStarted) {
                        break;
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                isVideoOver = true;
            }
        }).start();
    }

    @Override
    public void stop() {
        playerStarted = false;
        while (true) {
            if (isVideoOver & isAudioOver) {
                break;
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        audioRenderer.stop();
        videoRenderer.stop();
    }
}
