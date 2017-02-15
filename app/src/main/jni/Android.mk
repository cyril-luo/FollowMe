
LOCAL_PATH := $(call my-dir)

include $(CLEAR-VARS)

LOCAL_CPPFLAGS := -DEBUG

LOCAL_MODULE := TEEattest
LOCAL_MODULE_FILENAME := libTEEattest
LOCAL_SRC_FILES := tee_jni.c
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
