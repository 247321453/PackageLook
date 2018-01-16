package com.example.demo.tool;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {
    public static boolean copy(File src, File target) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        boolean error = false;
        try {
            fi = new FileInputStream(src);
            fo = new FileOutputStream(target);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (Throwable e) {
            e.printStackTrace();
            error = true;
        } finally {
            close(fi);
            close(fo);
            close(in);
            close(out);
        }
        return !error;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }


}
