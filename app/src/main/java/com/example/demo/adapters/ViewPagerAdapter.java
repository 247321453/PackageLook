package com.example.demo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final SparseArray<Fragment> fragments = new SparseArray<>();
    private Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    public void addFragment(int titleId, Fragment fragment) {
        fragments.put(titleId, fragment);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int strId = fragments.keyAt(position);
        return mContext.getString(strId);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.valueAt(position);
    }
}
