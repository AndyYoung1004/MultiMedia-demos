package com.example.multimedia.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AVPlayer implements IMediaPlayer {
    private static final String TAG = "AVPlayer";
    final int TIMEOUT_USEC = 10000;   // 10 毫秒
    private boolean isPlaying = false;
    private Surface surface;

    private VideoThread videoThread;
    private AudioThread audioThread;
    private String mediaPath;
    MediaExtractor videoExtractor, audioExtractor;
    MediaCodec videoCodec, audioCodec;
    AudioTrack audioTrack;

    public void releasePlayer() {
        isPlaying = false;
    }

    @Override
    public void setDataSource(String filePath) {
        this.mediaPath = filePath;
    }

    @Override
    public void setDisplay(Surface surface) {
        this.surface = surface;
    }

    @Override
    public void prepare() {
        {
            videoExtractor = new MediaExtractor();
            try {
                videoExtractor.setDataSource(mediaPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 获得视频所在的 轨道
            int trackIndex = getMediaTrackIndex(videoExtractor, "video/");
            if (trackIndex >=0) {
                MediaFormat format = videoExtractor.getTrackFormat(trackIndex);
                // 指定解码后的帧格式
                format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
                String mimeType = format.getString(MediaFormat.KEY_MIME);
                videoExtractor.selectTrack(trackIndex);
                try {
                    videoCodec = MediaCodec.createDecoderByType(mimeType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                videoCodec.configure(format, surface, null, 0);
            }
        }
        {
            audioExtractor = new MediaExtractor();
            try {
                audioExtractor.setDataSource(mediaPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = audioExtractor.getTrackFormat(i);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    audioExtractor.selectTrack(i);
                    int audioChannels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    int audioSampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int minBufferSize = AudioTrack.getMinBufferSize(audioSampleRate,
                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                            AudioFormat.ENCODING_PCM_16BIT);
                    int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                    int audioInputBufferSize = minBufferSize > 0 ? minBufferSize * 4 : maxInputSize;
                    int frameSizeInBytes = audioChannels * 2;
                    audioInputBufferSize = (audioInputBufferSize / frameSizeInBytes) * frameSizeInBytes;
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            audioSampleRate,
                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                            AudioFormat.ENCODING_PCM_16BIT,
                            audioInputBufferSize,
                            AudioTrack.MODE_STREAM);
                    audioTrack.play();
                    try {
                        audioCodec = MediaCodec.createDecoderByType(mime);
                        audioCodec.configure(mediaFormat, null, null, 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void start() {
        isPlaying = true;
        if (videoThread == null) {
            videoThread = new VideoThread();
            videoThread.start();
        }
        if (audioThread == null) {
            audioThread = new AudioThread();
            audioThread.start();
        }
    }

    @Override
    public void stop() {

    }

    // 处理视频通道
    private class VideoThread extends Thread {

        private boolean isVideoOver = false;

        @Override
        public void run() {
                videoCodec.start();
                MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
                ByteBuffer[] inputBuffers = videoCodec.getInputBuffers();
                long startMs = System.currentTimeMillis();
                while (!Thread.interrupted()) {
                    if (!isPlaying) {
                        return;
                    }

                    if (!isVideoOver) {
                        isVideoOver = putBufferToMediaCodec(videoExtractor, videoCodec, inputBuffers);
                    }

                    int outputBufferIndex = videoCodec.dequeueOutputBuffer(videoBufferInfo, TIMEOUT_USEC);
                    switch (outputBufferIndex) {
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            Log.v(TAG, "format changed");
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            Log.v(TAG, "timeout");
                            break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            //outputBuffers = videoCodec.getOutputBuffers();
                            Log.v(TAG, "output buffers changed");
                            break;
                        default:
                            sleepRender(videoBufferInfo, startMs);

                            videoCodec.releaseOutputBuffer(outputBufferIndex, true);
                            break;
                    }

                    if ((videoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.v(TAG, "buffer stream end");
                        break;
                    }
                }

                videoCodec.stop();
                videoCodec.release();
                videoExtractor.release();
        }
    }

    // 处理音频通道
    private class AudioThread extends Thread {

        @Override
        public void run() {
            audioCodec.start();
            //
            final ByteBuffer[] buffers = audioCodec.getOutputBuffers();
            int sz = buffers[0].capacity();
            byte[] mAudioOutTempBuf = new byte[sz];

            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer[] inputBuffers = audioCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = audioCodec.getOutputBuffers();
            boolean isAudioEOS = false;
            long startMs = System.currentTimeMillis();

            while (!Thread.interrupted()) {
                if (!isPlaying) {
                    return;
                }
                if (!isAudioEOS) {
                    isAudioEOS = putBufferToMediaCodec(audioExtractor, audioCodec, inputBuffers);
                }
                int outputBufferIndex = audioCodec.dequeueOutputBuffer(audioBufferInfo, TIMEOUT_USEC);
                switch (outputBufferIndex) {
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        Log.v(TAG, "format changed");
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        Log.v(TAG, "超时");
                        break;
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        outputBuffers = audioCodec.getOutputBuffers();
                        Log.v(TAG, "output buffers changed");
                        break;
                    default:
                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                        //延时操作
                        //如果缓冲区里的可展示时间>当前视频播放的进度，就休眠一下
                        sleepRender(audioBufferInfo, startMs);
                        if (audioBufferInfo.size > 0) {
                            if (mAudioOutTempBuf.length < audioBufferInfo.size) {
                                mAudioOutTempBuf = new byte[audioBufferInfo.size];
                            }
                            outputBuffer.position(0);
                            outputBuffer.get(mAudioOutTempBuf, 0, audioBufferInfo.size);
                            outputBuffer.clear();
                            if (audioTrack != null)
                                audioTrack.write(mAudioOutTempBuf, 0, audioBufferInfo.size);
                        }
                        audioCodec.releaseOutputBuffer(outputBufferIndex, false);
                        break;
                }

                if ((audioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.v(TAG, "buffer stream end");
                    break;
                }
            }//end while
            audioCodec.stop();
            audioCodec.release();
            audioExtractor.release();
            audioTrack.stop();
            audioTrack.release();
        }
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

    private boolean putBufferToMediaCodec(MediaExtractor extractor, MediaCodec decoder, ByteBuffer[] inputBuffers) {
        boolean isMediaEOS = false;
        int inputBufferIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            int sampleSize = extractor.readSampleData(inputBuffer, 0);
            if (sampleSize < 0) {
                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isMediaEOS = true;
                Log.v(TAG, "media eos");
            } else {
                decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                // MediaExtractor 准备下一个 单位的数据
                boolean ad = extractor.advance();
                if (!ad) {
                    isMediaEOS = false;
                }
            }
        }
        return isMediaEOS;
    }

    private void sleepRender(MediaCodec.BufferInfo audioBufferInfo, long startMs) {
        while (audioBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
            try {
                // 10 毫秒
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
