package com.example.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ListView mListView;
    AppAdapter mAppAdapter;
    private boolean mNeedRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.listview);
        mAppAdapter = new AppAdapter();
        mListView.setAdapter(mAppAdapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo appInfo = mAppAdapter.getItem(position);
            if (appInfo != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appInfo.pkg));
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Throwable e) {
                    Toast.makeText(this, "no find market", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mListView.setOnItemLongClickListener((parent, view, position, id) -> {
            AppInfo appInfo = mAppAdapter.getItem(position);
            if (appInfo != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + appInfo.pkg));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mNeedRefresh = true;
                } catch (Throwable e) {
                    Toast.makeText(this, "delete fail", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        loadApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNeedRefresh) {
            loadApps();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadApps();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadApps() {
        mNeedRefresh = false;
        ProgressDialog dialog = ProgressDialog.show(this, null, "loading app list");
        VUiKit.defer().when(() -> {
            List<AppInfo> appInfos = new ArrayList<>();
            PackageManager pm = getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            if (packageInfos != null) {
                for (PackageInfo packageInfo : packageInfos) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;
                    }
                    if (pm.getLaunchIntentForPackage(packageInfo.packageName) == null) {
                        continue;
                    }
                    if (getPackageName().equals(packageInfo.packageName)) {
                        continue;
                    }
                    AppInfo appInfo = new AppInfo();
                    appInfo.pkg = packageInfo.packageName;
                    appInfo.version = packageInfo.versionName + "   (" + packageInfo.versionCode + ")";
                    appInfo.label = String.valueOf(pm.getApplicationLabel(packageInfo.applicationInfo));
                    appInfo.icon = pm.getApplicationIcon(packageInfo.applicationInfo);
                    appInfos.add(appInfo);
                }
            }
            Collator collator = Collator.getInstance(Locale.CHINA);
            Collections.sort(appInfos, (o1, o2) -> collator.compare(o1.getLabel(), o2.getLabel()));
            return appInfos;
        }).fail((e) -> {
            dialog.dismiss();
        }).done((list) -> {
            dialog.dismiss();
            mAppAdapter.setAll(list);
            mAppAdapter.notifyDataSetChanged();
        });
    }

    private class AppInfo {
        Drawable icon;
        String label;
        String version;
        String pkg;

        public String getLabel() {
            if (label == null) return "";
            return label;
        }
    }

    private class ViewHolder {
        View view;

        public ViewHolder(View view) {
            this.view = view;
            view.setTag(this);
            icon = view.findViewById(R.id.img_icon);
            label = view.findViewById(R.id.tv_label);
            version = view.findViewById(R.id.tv_version);
            pkg = view.findViewById(R.id.tv_pkg);
        }

        final ImageView icon;
        final TextView label, version, pkg;
    }

    private class AppAdapter extends BaseAdapter {
        private final List<AppInfo> list = new ArrayList<>();

        public void setAll(List<AppInfo> appInfos) {
            list.clear();
            if (appInfos != null) {
                list.addAll(appInfos);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_app, parent, false);
                viewHolder = new ViewHolder(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AppInfo appInfo = getItem(position);
            viewHolder.label.setText(appInfo.getLabel());
            viewHolder.icon.setImageDrawable(appInfo.icon);
            viewHolder.version.setText(appInfo.version);
            viewHolder.pkg.setText(appInfo.pkg);
            return convertView;
        }
    }
}
