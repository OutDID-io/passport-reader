LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := gmpui
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/lib/libgmp.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gmpuii
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/lib/libgmp.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := rapidsnarkk
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/lib/librapidsnark.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/$(TARGET_ARCH_ABI)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := rapidsnark-wrapper
LOCAL_SRC_FILES := rapidsnark-wrapper.cpp
LOCAL_SHARED_LIBRARIES := rapidsnarkk gmpuii
LOCAL_WHOLE_STATIC_LIBRARIES := gmpui
LOCAL_LDLIBS := -landroid -llog
include $(BUILD_SHARED_LIBRARY)
