LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := MagicBeautify 
LOCAL_SRC_FILES := \
	MagicJni.cpp \
	./beautify/MagicBeautify.cpp \
	./bitmap/BitmapOperation.cpp \
	./bitmap/Conversion.cpp

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/bitmap \
	$(LOCAL_PATH)/beautify 

LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
include $(BUILD_SHARED_LIBRARY)
