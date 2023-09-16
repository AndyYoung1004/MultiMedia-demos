package com.example.multimedia.player;

import android.media.MediaCodec;
import android.view.Surface;

public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    private static final int TIMEOUT_USEC = 10000;
    MediaCodecVideoRenderer(String filePath, Surface surface) {
        super(filePath, "video", surface);
    }

    public void renderFrame() {
        queueBufferToCodec();
        outputData();
    }

    private void outputData() {
        int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
        switch (outputBufferIndex) {
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
            case MediaCodec.INFO_TRY_AGAIN_LATER:
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                break;
            default:
                codec.releaseOutputBuffer(outputBufferIndex, true);
                break;
        }
    }
}
