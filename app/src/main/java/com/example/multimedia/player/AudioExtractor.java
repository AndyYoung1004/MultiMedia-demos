package com.example.multimedia.player;

import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioExtractor implements IExtractor {
    private MediaExtractor extractor;
    private int trackIndex;
    private MediaFormat mediaFormat;
    private String mimeType;

    AudioExtractor() {
        extractor = new MediaExtractor();
        if (extractor == null) {
            throw new IllegalStateException("audio extractor null!!!");
        }
    }

    @Override
    public void setDataSource(String filePath) {
        try {
            extractor.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackNum = extractor.getTrackCount();
        for (int i = 0; i < trackNum; i++) {
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video")) {
                trackIndex = i;
                break;
            }
        }
        if (trackIndex >= 0) {
            mediaFormat = extractor.getTrackFormat(trackIndex);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatRGBFlexible);
            mimeType = mediaFormat.getString(MediaFormat.KEY_MIME);
            extractor.selectTrack(trackIndex);
        }
    }

    @Override
    public void selectTrack(int trackIndex) {
        extractor.selectTrack(trackIndex);
    }

    @Override
    public void release() {
        extractor.release();
    }

    @Override
    public int readSampleData(ByteBuffer inputBuffer) {
        return extractor.readSampleData(inputBuffer, 0);
    }

    @Override
    public long getSampleTime() {
        return extractor.getSampleTime();
    }

    @Override
    public boolean advance() {
        return extractor.advance();
    }

    @Override
    public MediaFormat getTrackFormat() {
        return mediaFormat;
    }
}
