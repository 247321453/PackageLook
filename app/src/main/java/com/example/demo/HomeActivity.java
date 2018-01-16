package com.example.demo;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.demo.adapters.ViewPagerAdapter;
import com.example.demo.fragments.AppListFragment;
import com.example.demo.fragments.HideAppFragment;
import com.example.demo.tool.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    MenuItem menuItem;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        GlobalSettings.get().init(this.getApplicationContext());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        mViewPager.addOnPageChangeListener(this);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);
                    return true;
//                case R.id.navigation_love_app:
//                    mViewPager.setCurrentItem(1);
//                    return true;
                case R.id.navigation_hide_app:
                    mViewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        });
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, getSupportFragmentManager());
        adapter.addFragment(R.string.title_app_list, new AppListFragment());
//        adapter.addFragment(R.string.title_love_app, new LoveAppFragment());
        adapter.addFragment(R.string.title_hide_app, new HideAppFragment());
        mViewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (menuItem != null) {
            menuItem.setChecked(false);
        } else {
            navigation.getMenu().getItem(0).setChecked(false);
        }
        menuItem = navigation.getMenu().getItem(position);
        menuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
