//
// Created by alin on 2021/12/15.
//

#ifndef HWDECODEDEMO_DECODEDEMO_H
#define HWDECODEDEMO_DECODEDEMO_H
#include <media/NdkMediaCodec.h>
#include <media/NdkMediaExtractor.h>
#include <media/NdkMediaFormat.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>


class DecodeDemo {
public:
    DecodeDemo() {}
    ~DecodeDemo();
    bool Release();
    bool Init(JNIEnv *jniEnv, jobject surface);
    bool Play(int sec = 999);

private:
//    AMediaFormat *format_[2] = {nullptr, nullptr};
    AMediaFormat *format_ = nullptr;
    AMediaExtractor *extractor_ = nullptr;
    AMediaCodec *codec_ = nullptr;
    int video_track_indx_ = -1;
    bool everplay_ = false;
    ANativeWindow*       m_ANativeWindow = nullptr;
};


#endif //HWDECODEDEMO_DECODEDEMO_H
