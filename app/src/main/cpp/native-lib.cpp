#include <jni.h>
#include <string.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <stdio.h>
#include <setjmp.h>
#include <math.h>
#include <stdint.h>
#include <time.h>
#include <string>
#include "md5/MD5.h"

//统一编译方式
extern "C" {
#include "jpeg/jpeglib.h"
#include "jpeg/cdjpeg.h"        /* Common decls for cjpeg/djpeg applications */
#include "jpeg/jversion.h"        /* for version message */
#include "jpeg/jconfig.h"
#include "jpeg/jerror.h"
#include "jpeg/cderror.h"
#include "jpeg/jversion.h"
#include "jpeg/config.h"
#include "jpeg/jinclude.h"
#include "jpeg/jmorecfg.h"
#include "base64/base64.h"
}

#include "aes/aes256.h"


// log打印
#define LOG_TAG "jni"
#define LOGW(...)  __android_log_write(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define true 1
#define false 0

typedef uint8_t BYTE;

// error 结构体
char *error;
struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

typedef struct my_error_mgr *my_error_ptr;

METHODDEF(void) my_error_exit(j_common_ptr cinfo) {
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    (*cinfo->err->output_message)(cinfo);

    error = (char *) myerr->pub.jpeg_message_table[myerr->pub.msg_code];
    LOGE("jpeg_message_table[%d]:%s", myerr->pub.msg_code,
         myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
    longjmp(myerr->setjmp_buffer, 1);
}

/**
 * jpeg压缩图片
 */
extern "C"
int generateJPEG(BYTE *data, int w, int h, int quality,
                 const char *outfilename, jboolean optimize) {

    // 结构体相当于Java类
    struct jpeg_compress_struct jcs;

    //当读完整个文件的时候就会回调my_error_exit这个退出方法。
    struct my_error_mgr jem;
    jcs.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;
    // setjmp是一个系统级函数，是一个回调。
    if (setjmp(jem.setjmp_buffer)) {
        return 0;
    }

    //初始化jsc结构体
    jpeg_create_compress(&jcs);
    //打开输出文件 wb 可写  rb 可读
    FILE *f = fopen(outfilename, "wb");
    if (f == NULL) {
        return 0;
    }
    //设置结构体的文件路径，以及宽高
    jpeg_stdio_dest(&jcs, f);
    jcs.image_width = w;
    jcs.image_height = h;

    // /* TRUE=arithmetic coding, FALSE=Huffman */
    jcs.arith_code = false;
    int nComponent = 3;
    /* 颜色的组成 rgb，三个 # of color components in input image */
    jcs.input_components = nComponent;
    //设置颜色空间为rgb
    jcs.in_color_space = JCS_RGB;
    ///* Default parameter setup for compression */
    jpeg_set_defaults(&jcs);
    //是否采用哈弗曼
    jcs.optimize_coding = optimize;
    //设置质量
    jpeg_set_quality(&jcs, quality, true);
    //开始压缩
    jpeg_start_compress(&jcs, TRUE);

    JSAMPROW row_pointer[1];
    int row_stride;
    row_stride = jcs.image_width * nComponent;
    while (jcs.next_scanline < jcs.image_height) {
        //得到一行的首地址
        row_pointer[0] = &data[jcs.next_scanline * row_stride];
        jpeg_write_scanlines(&jcs, row_pointer, 1);
    }
    // 压缩结束
    jpeg_finish_compress(&jcs);
    // 销毁回收内存
    jpeg_destroy_compress(&jcs);
    //关闭文件
    fclose(f);
    return 1;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_libjpeg_compress_ImageUtil_compressBitmap(JNIEnv *env, jclass type, jobject bitmap,
                                                   jint quality, jstring fileName_) {
    // 1.获取Bitmap信息
    AndroidBitmapInfo android_bitmap_info;
    AndroidBitmap_getInfo(env, bitmap, &android_bitmap_info);
    // 获取bitmap的 宽，高，format
    int bitmap_width = android_bitmap_info.width;
    int bitmap_height = android_bitmap_info.height;
    int format = android_bitmap_info.format;

    if (format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return -1;
    }
    // 2.解析Bitmap的像素信息，并转换成RGB数据,保存到二维byte数组里面
    BYTE *pixelscolor;
    // 2.1 锁定画布
    AndroidBitmap_lockPixels(env, bitmap, (void **) &pixelscolor);
    // 2.2 解析初始化参数值
    BYTE *data;
    BYTE r, g, b;
    data = (BYTE *) malloc(bitmap_width * bitmap_height * 3);//每一个像素都有三个信息RGB
    BYTE *tmpData;
    tmpData = data;//临时保存data的首地址
    int i = 0, j = 0;
    int color;
    //2.3 解析每一个像素点里面的rgb值(去掉alpha值)，保存到一维数组data里面
    for (i = 0; i < bitmap_height; ++i) {
        for (j = 0; j < bitmap_width; ++j) {
            //获取二维数组的每一个像素信息首地址
            color = *((int *) pixelscolor);
            r = ((color & 0x00FF0000) >> 16);
            g = ((color & 0x0000FF00) >> 8);
            b = ((color & 0x000000FF));
            //保存到data数据里面
            *data = b;
            *(data + 1) = g;
            *(data + 2) = r;
            data = data + 3;
            // 一个像素包括argb四个值，每+4就是取下一个像素点
            pixelscolor += 4;
        }
    }
    // 2.4. 解锁Bitmap
    AndroidBitmap_unlockPixels(env, bitmap);
    // jstring --> c char
    char *fileName = (char *) (env)->GetStringUTFChars(fileName_, 0);

    //3. 调用libjpeg核心方法实现压缩
    int resultCode = generateJPEG(tmpData, bitmap_width, bitmap_height, quality, fileName,
                                  true);

    //4.释放资源
    env->ReleaseStringUTFChars(fileName_, fileName);
    free((void *) tmpData);
    // 4.2 释放Bitmap
    // 4.2.1 通过对象获取类
    jclass bitmap_clz = env->GetObjectClass(bitmap);
    // 4.2.2 通过类和方法签名获取方法id
    jmethodID recycle_mid = env->GetMethodID(bitmap_clz, "recycle", "()V");
    // 4.2.3 执行回收释放方法
    env->CallVoidMethod(bitmap, recycle_mid);

    // 5.返回结果
    if (resultCode == 0) {
        return -1;
    }
    return 1;
}

// 加密的秘钥
char password[] = "Big god take me fly!";

// 加密文件
void crypt_file(char *normal_path, char *crypt_path) {
    //打开文件
    FILE *normal_fp = fopen(normal_path, "rb");
    FILE *crypt_fp = fopen(crypt_path, "wb");
    //一次读取一个字符
    int ch;
    int i = 0; //循环使用密码中的字母进行异或运算
    int pwd_len = strlen(password); //密码的长度
    while ((ch = fgetc(normal_fp)) != EOF) { //End of File
        //写入（异或运算）
        fputc(ch ^ password[i % pwd_len], crypt_fp);
        i++;
    }
    // 关闭
    fclose(crypt_fp);
    fclose(normal_fp);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_libjpeg_compress_ImageUtil_cryptFile(JNIEnv *env, jclass type, jstring filePath_,
                                              jstring cryptPath_) {
    char *filePath = (char *) env->GetStringUTFChars(filePath_, 0);
    char *cryptPath = (char *) env->GetStringUTFChars(cryptPath_, 0);

    crypt_file(filePath, cryptPath);

    env->ReleaseStringUTFChars(filePath_, filePath);
    env->ReleaseStringUTFChars(cryptPath_, cryptPath);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_encode_AesMd5Encode_getMd5(JNIEnv *env, jclass type, jstring origin_) {


    const char *originStr;
    //将jstring转化成char *类型
    originStr = env->GetStringUTFChars(origin_, false);

    MD5 md5 = MD5(originStr);
    std::string md5Result = md5.hexdigest();

    env->ReleaseStringUTFChars(origin_, originStr);
    //将char *类型转化成jstring返回给Java层
    return env->NewStringUTF(md5Result.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_encode_AesMd5Encode_Base64Encode(JNIEnv *env, jobject instance, jstring msg_) {

    // 字符长度
    size_t length = (size_t) env->GetStringUTFLength(msg_);
    // 字符内容
    char *c_msg = (char *) env->GetStringUTFChars(msg_, 0);

    // 加密后的数组
    char *result = base64_encode(c_msg, length);

    env->ReleaseStringUTFChars(msg_, c_msg);

    // 加密后的字符串
    return env->NewStringUTF(result);

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_encode_AesMd5Encode_Base64Decode(JNIEnv *env, jobject instance, jstring msg_) {

    // 解密字符串的 长度
    size_t length = (size_t) env->GetStringUTFLength(msg_);
    // 解密字符串的 内容
    char *c_msg = (char *) env->GetStringUTFChars(msg_, 0);

    // 解密后的字符串数组
    const char *result = base64_decode(c_msg, length);
    env->ReleaseStringUTFChars(msg_, c_msg);

    // 解密后的字符串
    return env->NewStringUTF(result);
}

//密钥
unsigned char key[32] = {0x11, 0x3c, 0x7a, 0xb5, 0xa7, 0xcc, 0x4d, 0x6c,
                         0x7c, 0x0a, 0x54, 0xb4, 0xb4, 0x3e, 0x28, 0x81,
                         0x8d, 0x9b, 0x15, 0xd4, 0x31, 0xb6, 0x48, 0x88,
                         0x89, 0x6c, 0xaa, 0x81, 0x95, 0x14, 0xad, 0x72};

//初始化向量
uint8_t iv[16] = {15, 10, 1, 2, 14, 3, 13, 4, 2, 5, 8, 6, 10, 7, 9, 8};

extern "C"
JNIEXPORT jstring JNICALL
Java_com_encode_AesMd5Encode_AesEncode(JNIEnv *env, jclass type, jstring src_) {

    //****************************************开始加密******************************************************
    //1.初始化数据
    //初始化加密参数
    aes256_context ctx;
    aes256_init(&ctx, key);

    //2.将jstring转为char
    const char *src = env->GetStringUTFChars(src_, false);

    //3.分组填充加密
    //小于16字节，填充16字节，后面填充几个几 比方说10个字节 就要补齐6个6 11个字节就补齐5个5
    //如果是16的倍数，填充16字节，后面填充0x10
    int len = strlen(src);
    int remainder = len % 16;
    int group = len / 16;
    int size = 16 * (group + 1);

    uint8_t input[size];
    for (int i = 0; i < size; i++) {
        if (i < len) {
            input[i] = src[i];
        } else {
            if (remainder == 0) {
                input[i] = 0x10;
            } else {    //如果不足16位 少多少位就补几个几  如：少4为就补4个4 以此类推
                int dif = size - len;
                input[i] = dif;
            }
        }
    }
    //释放src
    env->ReleaseStringUTFChars(src_, src);

    //加密
    uint8_t output[size];
    aes256_encrypt_cbc(&ctx, input, size, iv, output);

    //base64加密后然后jstring格式输出
    char *enc = base64_encode(reinterpret_cast<const char *>(output), sizeof(output));
    jstring entryptString = env->NewStringUTF(enc);
    free(enc);

    return entryptString;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_encode_AesMd5Encode_AesDecode(JNIEnv *env, jclass type, jstring base64_) {

    //将jstring转为char
    const char *base64 = env->GetStringUTFChars(base64_, false);
    //base64解密
    char *src = base64_decode(base64, strlen(base64));
    //释放base64
    env->ReleaseStringUTFChars(base64_, base64);

    //获取填充密文长度
    int len = strlen(src);
    //设置明文数组
    unsigned char *out = (unsigned char *) malloc(len);
    //ase解密
    aes256_context ctx;
    aes256_init(&ctx, key);
    aes256_decrypt_cbc(&ctx, reinterpret_cast<unsigned char *>(src), len, iv, out);
    //释放src
    free(src);

    //获取padding
    int padding = out[len - 1];
    int dlen = len - padding;
    if (dlen <= 0) {
        free(out);
        return NULL;
    }

    //排除padding获取明文
    unsigned char *dst = (unsigned char *) malloc(dlen);
    memcpy(dst, out, dlen);
    free(out);

    return env->NewStringUTF(reinterpret_cast<const char *>(dst));
}