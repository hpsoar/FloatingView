package com.beacon.android;

/**
 * Created by huangpeng on 5/1/15.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.anthonyfernandez.floatingmenu.R;

public class GridAdapter extends BaseAdapter{
    private List<ResolveInfo> appItems = new ArrayList<>();
    private Context mContext;
    public GridAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return appItems.size();
    }

    @Override
    public Object getItem(int position) {
        return appItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.launcher_item, null);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.icon);
        TextView text = (TextView) convertView.findViewById(R.id.text);
        ResolveInfo item = (ResolveInfo)getItem(position);
        PackageManager pm = parent.getContext().getPackageManager();
        image.setImageDrawable(item.loadIcon(pm));
        text.setText(item.loadLabel(pm).toString());
        return convertView;
    }

    public void setAppItems(List<ResolveInfo> items) {
        this.appItems = items;
        notifyDataSetChanged();
    }
}
