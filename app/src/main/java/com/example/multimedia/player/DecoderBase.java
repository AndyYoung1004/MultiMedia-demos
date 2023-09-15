package com.example.multimedia.player;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;

public class DecoderBase implements IDecoder {
    private MediaCodec codec;
    @Override
    public MediaCodec createDecoderByType(String mime) {
        if (codec != null) {
            throw new IllegalStateException("codec not null!!!");
        }
        try {
            codec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codec;
    }

    @Override
    public void configure(MediaFormat format, Surface surface, MediaCrypto mediaCrypto, int flags) {
        if (codec == null) {
            throw new IllegalStateException("codec null!!!");
        }
        codec.configure(format, surface, mediaCrypto, flags);
    }

    @Override
    public void start() {
        if (codec == null) {
            throw new IllegalStateException("codec null!!!");
        }
        codec.start();
    }
}
