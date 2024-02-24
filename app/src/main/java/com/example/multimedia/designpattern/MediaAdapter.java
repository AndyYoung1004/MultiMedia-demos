package com.example.multimedia.designpattern;

import android.provider.MediaStore;

public class MediaAdapter implements IPlayerInterface{
    IAdvancedPlayerInterface player;
    private String type;

    public MediaAdapter(String type) {
        this.type = type;
        if (type.equals("audio")) {
            player = new AudioPlayer();
        } else if (type.equals("video")) {
            player = new VideoPlayer();
        }
    }

    @Override
    public void play() {
        if (type.equals("audio")) {
            player.playAudio();
        } else if (type.equals("video")) {
            player.playVideo();
        }

    }
}
