LOCAL_PATH = $(call my-dir)
include $(CLEAR_VARS)
//模块的名称
LOCAL_MODULE    :=libjpeg
//编译的源文件
LOCAL_SRC_FILES :=libjpeg.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE    :=compressimg
LOCAL_SRC_FILES :=compress_image.cpp
LOCAL_SHARED_LIBRARIES  :=libjpeg
LOCAL_LDLIBS    :=-ljnigraphics -llog
include $(BUILD_SHARED_LIBRARY)
