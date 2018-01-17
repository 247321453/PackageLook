package com.example.demo.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.demo.adapters.AppAdapter;
import com.example.demo.bean.AppInfo;
import com.example.demo.GlobalSettings;
import com.example.demo.R;
import com.example.demo.tool.FileUtils;
import com.example.demo.tool.VUiKit;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AppListFragment extends BaseFragment {
    ListView mListView;
    AppAdapter mAppAdapter;

    private boolean mNeedRefresh = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        mListView = view.findViewById(R.id.listview);
        mAppAdapter = new AppAdapter(getContext());
        mListView.setAdapter(mAppAdapter);
        mListView.setOnItemClickListener((parent, v, position, id) -> {
            AppInfo appInfo = mAppAdapter.getItem(position);
            showPopupMenu(v, appInfo);
        });
        mListView.setOnItemLongClickListener((parent, v, position, id) -> {
            AppInfo appInfo = mAppAdapter.getItem(position);
            if (appInfo != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + appInfo.pkg));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Throwable e) {
                    Toast.makeText(getContext(), R.string.delete_pkg_fail, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNeedRefresh) {
            mNeedRefresh = false;
            loadApps();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        getContext().registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(GlobalSettings.ACTION_SHOW_APP_CHANGED);
        getContext().registerReceiver(mReceiver, filter2);
    }

    @Override
    public void onStop() {
        getContext().unregisterReceiver(mReceiver);
        super.onStop();
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isPausing()) {
                loadApps();
            } else {
                mNeedRefresh = true;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_app_list, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadApps();
                break;
            case R.id.action_sort_pkg:
                GlobalSettings.get().setSortByName(false);
                loadApps();
                break;
            case R.id.action_sort_name:
                GlobalSettings.get().setSortByName(true);
                loadApps();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu(View view, AppInfo appInfo) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_app_list_long);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_launch: {
                    Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(appInfo.pkg);
                    try {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Throwable e) {
                        Toast.makeText(getContext(), R.string.error_start_app_fail, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.action_market: {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appInfo.pkg));
                    try {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Throwable e) {
                        Toast.makeText(getContext(), R.string.no_find_market, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case R.id.action_detail: {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + appInfo.pkg));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Throwable e) {
                        Toast.makeText(getContext(), R.string.delete_pkg_fail, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.action_backup: {
                    backup(appInfo);
                }
                break;
                case R.id.action_hide: {
                    GlobalSettings.get().addHide(appInfo.pkg);
                    GlobalSettings.get().saveHide();
                    mAppAdapter.remove(appInfo);
                    mAppAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(GlobalSettings.ACTION_HIDE_APP_CHANGED, Uri.parse("package:" + appInfo.pkg));
                    intent.setPackage(getContext().getPackageName());
                    getContext().sendBroadcast(intent);
                }
                break;
            }
            return false;
        });
        popupMenu.show();
    }

    private void loadApps() {
        ProgressDialog dialog = ProgressDialog.show(getContext(), null, getString(R.string.loading_app));
        VUiKit.defer().when(() -> {
            List<AppInfo> appInfos = new ArrayList<>();
            PackageManager pm = getContext().getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            if (packageInfos != null) {
                for (PackageInfo packageInfo : packageInfos) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;
                    }
                    if (pm.getLaunchIntentForPackage(packageInfo.packageName) == null) {
                        continue;
                    }
                    if (getContext().getPackageName().equals(packageInfo.packageName)) {
                        continue;
                    }
                    if (GlobalSettings.get().isHide(packageInfo.packageName)) {
                        continue;
                    }
                    AppInfo appInfo = new AppInfo();
                    appInfo.pkg = packageInfo.packageName;
                    appInfo.version = packageInfo.versionName + "   (" + packageInfo.versionCode + ")";
                    appInfo.label = String.valueOf(pm.getApplicationLabel(packageInfo.applicationInfo));
                    appInfo.icon = pm.getApplicationIcon(packageInfo.applicationInfo);
                    appInfo.applicationInfo = packageInfo.applicationInfo;
                    appInfos.add(appInfo);
                }
            }
            Collator collator = Collator.getInstance(Locale.CHINA);
            if (GlobalSettings.get().isSortByName()) {
                Collections.sort(appInfos, (o1, o2) -> collator.compare(o1.getLabel(), o2.getLabel()));
            } else {
                Collections.sort(appInfos, (o1, o2) -> collator.compare(o1.getPackage(), o2.getPackage()));
            }
            return appInfos;
        }).fail((e) -> {
            dialog.dismiss();
        }).done((list) -> {
            dialog.dismiss();
            mAppAdapter.setAll(list);
            mAppAdapter.notifyDataSetChanged();
        });
    }

    private void backup(AppInfo appInfo) {
        ProgressDialog dialog = ProgressDialog.show(getContext(), null, getString(R.string.tip_start_backup));
        File target = new File(Environment.getExternalStorageDirectory(), appInfo.label + "_" + appInfo.pkg + ".apk");
        VUiKit.defer().when(() -> {
            return FileUtils.copy(new File(appInfo.applicationInfo.publicSourceDir), target);
        }).done((rs) -> {
            dialog.dismiss();
            if (rs) {
                Toast.makeText(getContext(), getString(R.string.back_ok) + "\n" + target.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.back_fail, appInfo.label), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
