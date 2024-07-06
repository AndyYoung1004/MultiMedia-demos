package com.example.multimedia.player;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaCodecRenderer {
    private static final int TIMEOUT_USEC = 10000;
    private IExtractor extractor;
    public MediaCodec codec;
    public MediaFormat format;
    private long timeStamp;
    public MediaCodec.BufferInfo bufferInfo;
    private Surface surface;

    MediaCodecRenderer(String filePath, String mime, Surface surface, IExtractor extractor) {
        this.surface = surface;
        this.bufferInfo = new MediaCodec.BufferInfo();
        this.extractor = extractor;
        this.extractor.setDataSource(filePath);
        this.extractor.setFileType(mime);
    }

    public void start() {
        codec.start();
    }

    public void stop() {
        codec.stop();
        codec.release();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void initMediaCodecRenderer() {
        format = extractor.init();
        initCodec();
    }

    private void initCodec() {
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        String mimeType = format.getString(MediaFormat.KEY_MIME);
        try {
            codec = MediaCodec.createDecoderByType(mimeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        codec.configure(format, surface, null, 0);
    }

    public void renderFrame() {
        queueBufferToCodec();
        outputData();
    }

    private boolean queueBufferToCodec() {
        boolean isEos = false;
        int inputBufferIndex = codec.dequeueInputBuffer(TIMEOUT_USEC);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
            int sampleSize = extractor.readSampleData(inputBuffer);
            if (sampleSize < 0) {
                codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isEos = true;
            } else {
                timeStamp = extractor.getSampleTime();
                codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, timeStamp, 0);
                if (!extractor.advance()) {
                    isEos = true;
                }
            }
        }
        return isEos;
    }

    public void outputData() {
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
