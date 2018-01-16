package com.example.demo.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
    private boolean mPausing = true;

    @Override
    public void onResume() {
        super.onResume();
        mPausing = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPausing = true;
    }

    public boolean isPausing() {
        return mPausing;
    }
}
