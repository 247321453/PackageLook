package com.example.demo;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalSettings {
    private static final GlobalSettings sGlobalSettings = new GlobalSettings();
    private SharedPreferences mSettings;
    private final List<String> mHide = new ArrayList<>();
    private final List<String> mLove = new ArrayList<>();
    private final static String PREF_KEY_HIDE_APP = "hide_app";
    private final static String PREF_KEY_LOVE_APP = "love_app";
    private final static String PREF_KEY_SORT = "app_sort_pkg";
    public static final String ACTION_HIDE_APP_CHANGED = "com.kk.action.app_list_changed.hide";
    public static final String ACTION_SHOW_APP_CHANGED = "com.kk.action.app_list_changed.show";

    public static GlobalSettings get() {
        return sGlobalSettings;
    }

    public void init(Context context) {
        if (mSettings == null) {
            mSettings = context.getSharedPreferences("app_config", Context.MODE_PRIVATE);
            Set<String> pkgs = mSettings.getStringSet(PREF_KEY_HIDE_APP, null);
            mHide.clear();
            if (pkgs != null) {
                mHide.addAll(pkgs);
            }
            pkgs = mSettings.getStringSet(PREF_KEY_LOVE_APP, null);
            mLove.clear();
            if (pkgs != null) {
                mLove.addAll(pkgs);
            }
        }
    }

    public boolean isSortByName() {
        return mSettings.getInt(PREF_KEY_SORT, 1) > 0;
    }

    public void setSortByName(boolean byName) {
        if (byName) {
            mSettings.edit().putInt(PREF_KEY_SORT, 1).apply();
        } else {
            mSettings.edit().putInt(PREF_KEY_SORT, 0).apply();
        }
    }

    public List<String> getHideList() {
        return mHide;
    }

    public boolean isLove(String pkg) {
        synchronized (mLove) {
            return mLove.contains(pkg);
        }
    }

    public void addLove(String pkg) {
        synchronized (mLove) {
            if (!mLove.contains(pkg)) {
                mLove.add(pkg);
            }
        }
    }

    public void removeLove(String pkg) {
        synchronized (mLove) {
            mLove.remove(pkg);
        }
    }

    public boolean isHide(String pkg) {
        synchronized (mHide) {
            return mHide.contains(pkg);
        }
    }

    public void addHide(String pkg) {
        synchronized (mHide) {
            if (!mHide.contains(pkg)) {
                mHide.add(pkg);
            }
        }
    }

    public void removeHide(String pkg) {
        synchronized (mHide) {
            mHide.remove(pkg);
        }
    }

    public void saveLove() {
        Set<String> pkgs = new HashSet<>();
        pkgs.addAll(mLove);
        mSettings.edit().putStringSet(PREF_KEY_LOVE_APP, pkgs).apply();
    }

    public void saveHide() {
        Set<String> pkgs = new HashSet<>();
        pkgs.addAll(mHide);
        mSettings.edit().putStringSet(PREF_KEY_HIDE_APP, pkgs).apply();
    }
}
