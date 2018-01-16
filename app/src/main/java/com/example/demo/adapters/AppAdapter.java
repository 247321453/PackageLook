package com.example.demo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demo.GlobalSettings;
import com.example.demo.R;
import com.example.demo.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends BaseAdapter {
    private class ViewHolder {
        View view;

        public ViewHolder(View view) {
            this.view = view;
            view.setTag(this);
            icon = view.findViewById(R.id.img_icon);
            label = view.findViewById(R.id.tv_label);
            version = view.findViewById(R.id.tv_version);
            pkg = view.findViewById(R.id.tv_pkg);
            star = view.findViewById(R.id.chk_star);
            no = view.findViewById(R.id.tv_no);
        }

        final ImageView icon;
        final ImageView star;
        final TextView no, label, version, pkg;
    }

    private final List<AppInfo> list = new ArrayList<>();

    public void setAll(List<AppInfo> appInfos) {
        list.clear();
        if (appInfos != null) {
            list.addAll(appInfos);
        }
    }

    private LayoutInflater layoutInflater;

    public AppAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public boolean remove(AppInfo appInfo) {
        return list.remove(appInfo);
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_app, parent, false);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AppInfo appInfo = getItem(position);
        viewHolder.label.setText(appInfo.getLabel());
        viewHolder.icon.setImageDrawable(appInfo.icon);
        viewHolder.version.setText(appInfo.version);
        viewHolder.pkg.setText(appInfo.pkg);
        viewHolder.star.setSelected(GlobalSettings.get().isLove(appInfo.pkg));
        viewHolder.star.setOnClickListener(v -> {
            boolean star = GlobalSettings.get().isLove(appInfo.pkg);
            if (star) {
                GlobalSettings.get().removeLove(appInfo.pkg);
                viewHolder.star.setSelected(false);
            } else {
                GlobalSettings.get().addLove(appInfo.pkg);
                viewHolder.star.setSelected(true);
            }
            GlobalSettings.get().saveLove();
        });
        viewHolder.no.setText((1 + position)+".");
        return convertView;
    }
}
