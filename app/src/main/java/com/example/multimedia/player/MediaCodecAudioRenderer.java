package com.example.multimedia.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaCodecAudioRenderer {

    private static final int TIMEOUT_USEC = 10000;
    private MediaExtractor extractor;
    private MediaCodec codec;
    private String filePath;
    private long timeStamp;
    private AudioTrack track;
    MediaFormat format;
    MediaCodec.BufferInfo bufferInfo;
    byte[] audioTempBuf;

    MediaCodecAudioRenderer(String filePath) {
        this.filePath = filePath;
        audioTempBuf = new byte[10000];
        bufferInfo = new MediaCodec.BufferInfo();
    }

    void init() {
        extractor = new MediaExtractor();
        try {
            extractor.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int trackIndex = getMediaTrackIndex(extractor, "audio/");
        if (trackIndex >= 0) {
            extractor.selectTrack(trackIndex);
            format = extractor.getTrackFormat(trackIndex);
            String mimeType = format.getString(MediaFormat.KEY_MIME);
            int audioChannels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            int audioSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            int minBufferSize = AudioTrack.getMinBufferSize(audioSampleRate,
                    (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                    AudioFormat.ENCODING_PCM_16BIT);
            int maxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            int audioInputBufferSize = minBufferSize > 0 ? minBufferSize * 4 : maxInputSize;
            int frameSizeInBytes = audioChannels * 2;
            audioInputBufferSize = (audioInputBufferSize / frameSizeInBytes) * frameSizeInBytes;
            track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    audioSampleRate,
                    (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                    AudioFormat.ENCODING_PCM_16BIT,
                    audioInputBufferSize,
                    AudioTrack.MODE_STREAM);
            track.play();
            try {
                codec = MediaCodec.createDecoderByType(mimeType);
                codec.configure(format, null, null, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferIndex);
                if (bufferInfo.size > 0) {
                    if (audioTempBuf.length < bufferInfo.size) {
                        audioTempBuf = new byte[bufferInfo.size];
                    }
                    outputBuffer.position(0);
                    outputBuffer.get(audioTempBuf, 0, bufferInfo.size);
                    outputBuffer.clear();
                    if (track != null)
                        track.write(audioTempBuf, 0, bufferInfo.size);
                }
                codec.releaseOutputBuffer(outputBufferIndex, false);
                break;
        }
    }
}
