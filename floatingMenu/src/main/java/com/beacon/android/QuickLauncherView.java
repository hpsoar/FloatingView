package com.beacon.android;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.anthonyfernandez.floatingmenu.R;

public class QuickLauncherView extends RelativeLayout {
    private GridView gridView;
    private GridAdapter gridAdapter;

    public QuickLauncherView(Context context) {
        super(context);
        init();
    }

    public QuickLauncherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuickLauncherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.quick_launcher_view, this);

        this.gridView = (GridView)findViewById(R.id.quick_launcher_grid_view);

        this.gridAdapter = new GridAdapter(getContext());

        this.gridView.setAdapter(this.gridAdapter);
    }
}