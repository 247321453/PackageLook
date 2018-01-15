#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

extern "C" {
jstring charTojstring(JNIEnv *env, const char *pat) {
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte *) pat);
    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("GB2312");
    //将byte数组转换为java String,并输出
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}
char *jstringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}
}
extern "C"
JNIEXPORT void

JNICALL
Java_com_kk_jnis_JniTest_writeFile(
        JNIEnv *env,
        jclass clazz,
        jstring _path,
        jstring _msg) {
    FILE *fp = NULL;
    const char *path = jstringToChar(env, _path);
    const char *msg = jstringToChar(env, _msg);
    fp = fopen(path, "a");

    fprintf(fp, "%s\n", msg);

    fclose(fp);
}
extern "C"
JNIEXPORT jstring

JNICALL
Java_com_kk_jnis_JniTest_readFile(
        JNIEnv *env,
        jclass clazz,
        jstring _path) {
    FILE *fp = NULL;
    const char *path = jstringToChar(env, _path);
    fp = fopen(path, "r");
    char line[1024];
    size_t len = fread(line, sizeof(char), 1024, fp);
    line[len] = '\0';
    return charTojstring(env, line);
}