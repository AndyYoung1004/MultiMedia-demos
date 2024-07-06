package com.example.multimedia.player;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AVExtractor implements IExtractor {
    private MediaExtractor extractor;
    private String filePath;
    private String avType;
    private MediaFormat format;

    AVExtractor() {
    }

    @Override
    public void setDataSource(String input) {
        this.filePath = input;
    }

    @Override
    public void setFileType(String type) {
        this.avType = type;
    }

    @Override
    public MediaFormat init() {
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackIndex = getMediaTrackIndex(avType);
        if (trackIndex >= 0) {
            extractor.selectTrack(trackIndex);
            format = extractor.getTrackFormat(trackIndex);
            return format;
        }
        return null;
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
    public int readSampleData(ByteBuffer inputBuffer) {
        return extractor.readSampleData(inputBuffer, 0);
    }

    private int getMediaTrackIndex(String mimeType) {
        int trackIndex = -1;
        int trackNum = extractor.getTrackCount();
        for (int i = 0; i < trackNum; i++) {
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(mimeType)) {
                trackIndex = i;
                break;
            }
        }
        return trackIndex;
    }
}
