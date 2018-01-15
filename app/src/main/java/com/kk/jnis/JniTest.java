package com.kk.jnis;

public class JniTest {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void writeFile(String path, String str);

    public static native String readFile(String path);
}
