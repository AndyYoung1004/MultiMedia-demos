package com.example.multimedia.player;

import android.view.Surface;

public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    MediaCodecVideoRenderer(String filePath, Surface surface, IExtractor extractor) {
        super(filePath, "video", surface, extractor);
    }
}
