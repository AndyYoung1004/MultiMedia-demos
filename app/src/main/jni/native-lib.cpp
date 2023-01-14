#include <jni.h>
#include <string>
#include "DecodeDemo.h"

static DecodeDemo* decoder = nullptr;
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_multimedia_NDKPlayerActivity_init(JNIEnv* env, jobject,jobject surface) {
    decoder = new DecodeDemo();
    if (decoder->Init(env, surface)) {
        return JNI_TRUE;
    } else {
        delete decoder;
        decoder = nullptr;
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_multimedia_NDKPlayerActivity_play(JNIEnv* env, jobject, jint sec) {
    if (decoder && decoder->Play(sec)) {
        return JNI_TRUE;
    } else {
        delete decoder;
        decoder = nullptr;
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_multimedia_NDKPlayerActivity_release(JNIEnv* env, jobject) {
    if (decoder && decoder->Release()) {
        delete decoder;
        decoder = nullptr;
        return JNI_TRUE;
    } else {
        delete decoder;
        decoder = nullptr;
        return JNI_FALSE;
    }
}