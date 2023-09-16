package com.example.multimedia.player;

import android.view.Surface;

public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    MediaCodecVideoRenderer(String filePath, Surface surface) {
        super(filePath, "video", surface);
    }
}
