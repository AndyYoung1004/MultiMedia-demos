package com.example.multimedia.player;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.view.Surface;

public interface IDecoder {
    MediaCodec createDecoderByType(String mime);
    void configure(MediaFormat format, Surface surface, MediaCrypto mediaCrypto, int flags);
    void start();
}
