package com.example.multimedia.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import java.nio.ByteBuffer;

public class MediaCodecAudioRenderer extends MediaCodecRenderer {
    private static final int TIMEOUT_USEC = 10000;
    private AudioTrack track;
    private byte[] audioTempBuf;

    MediaCodecAudioRenderer(String filePath) {
        super(filePath, "audio", null);
        audioTempBuf = new byte[10000];
    }

    public void initAudioTrack() {
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
    }

    public void outputData() {
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
