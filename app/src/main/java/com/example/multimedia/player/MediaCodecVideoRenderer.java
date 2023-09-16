package com.example.multimedia.player;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaCodecVideoRenderer {
    private static final int TIMEOUT_USEC = 10000;
    private MediaExtractor extractor;
    private MediaCodec codec;
    private String filePath;
    private Surface surface;
    private long timeStamp;
    MediaFormat format;
    MediaCodec.BufferInfo bufferInfo;

    MediaCodecVideoRenderer(String filePath, Surface surface) {
        this.filePath = filePath;
        this.surface = surface;
        bufferInfo = new MediaCodec.BufferInfo();
    }

    public void init() {
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackIndex = getMediaTrackIndex(extractor, "video/");
        if (trackIndex >=0) {
            format = extractor.getTrackFormat(trackIndex);
            // 指定解码后的帧格式
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            String mimeType = format.getString(MediaFormat.KEY_MIME);
            extractor.selectTrack(trackIndex);
            try {
                codec = MediaCodec.createDecoderByType(mimeType);
            } catch (IOException e) {
                e.printStackTrace();
            }
            codec.configure(format, surface, null, 0);
        }
    }

    public void start() {
        codec.start();
    }

    public void renderFrame() {
        putBufferToCodec();
        outputData();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    private int getMediaTrackIndex(MediaExtractor videoExtractor, String MEDIA_TYPE) {
        int trackIndex = -1;
        // 获得轨道数量
        int trackNum = videoExtractor.getTrackCount();
        for (int i = 0; i < trackNum; i++) {
            MediaFormat mediaFormat = videoExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(MEDIA_TYPE)) {
                trackIndex = i;
                break;
            }
        }
        return trackIndex;
    }

    private boolean putBufferToCodec() {
        boolean isEos = false;
        int inputBufferIndex = codec.dequeueInputBuffer(TIMEOUT_USEC);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
            int sampleSize = extractor.readSampleData(inputBuffer, 0);
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
