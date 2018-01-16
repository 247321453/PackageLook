package com.example.demo.bean;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2018/1/16 0016.
 */
public class AppInfo {
    public transient Drawable icon;
    public transient String label;
    public transient ApplicationInfo applicationInfo;
    public String version;
    public String pkg;

    public String getLabel() {
        if (label == null) return "";
        return label;
    }

    public String getPackage() {
        if (pkg == null) return "";
        return pkg;
    }
}
