package com.beacon.android;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import fr.anthonyfernandez.floatingmenu.Manager.PInfo;
import fr.anthonyfernandez.floatingmenu.Manager.RetrievePackages;
import fr.anthonyfernandez.floatingmenu.R;

public class QuickLauncherView extends RelativeLayout {
    private static String TAG = "QuickLauncherView";
    private GridView gridView;
    private GridAdapter gridAdapter;
    private int anchorX, anchorY;
    private boolean inToggleAnimation = false;

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

        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageManager manager = getContext().getPackageManager();
                try {
                    PInfo app = (PInfo) gridAdapter.getItem(position);
                    Intent intent = manager.getLaunchIntentForPackage(app.pname);
                    if (intent == null)
                        throw new PackageManager.NameNotFoundException();
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    getContext().startActivity(intent);
                    toggleVisibility();
                } catch (PackageManager.NameNotFoundException e) {

                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.print("hello");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.print(""+event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME || event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (getVisibility() == View.VISIBLE) {
                toggleVisibility();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void setAnchor(int cx, int cy) {
        this.anchorX = cx;
        this.anchorY = cy;
    }

    public void toggleVisibility() {
        if (this.getVisibility() == View.VISIBLE) {
            this.hideTo(anchorX, anchorY);
        }
        else {
            this.loadApps();
            this.showFrom(anchorX, anchorY);
        }
    }

    private void showFrom(int cx, int cy) {
        float radius = Math.max(this.getWidth(), this.getHeight());// * 2.0f;

        this.setVisibility(View.VISIBLE);
        Animator reveal = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0, radius);
        reveal.setInterpolator(new AccelerateInterpolator(2.0f));
        reveal.setDuration(200);
        reveal.start();
    }

    private void hideTo(int cx, int cy) {
        if (inToggleAnimation) return;

        inToggleAnimation = true;
        float radius = Math.max(this.getWidth(), this.getHeight());// * 2.0f;
        Animator reveal = ViewAnimationUtils.createCircularReveal(this, cx, cy, radius, 0);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                QuickLauncherView.this.setVisibility(View.INVISIBLE);
                inToggleAnimation = false;
            }
        });
        reveal.setDuration(200);
        reveal.start();
    }

    private List<PInfo> getAppList() {
        RetrievePackages getInstalledPackages = new RetrievePackages(getContext().getApplicationContext());
        List<String> mostFrequentlyUsedPackages = UStats.mostFrequentlyUsedPackages(getContext().getApplicationContext());
        if (mostFrequentlyUsedPackages.isEmpty()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                getContext().getApplicationContext().startActivity(intent);
            }
            catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
        if (mostFrequentlyUsedPackages.size() > 20) {
            mostFrequentlyUsedPackages = mostFrequentlyUsedPackages.subList(0, 20);
        }

        return getInstalledPackages.test2(mostFrequentlyUsedPackages);
//        return getInstalledPackages.test(mostFrequentlyUsedPackages, 20);
    }

    public void loadApps() {
        if (this.gridAdapter.getCount() == 0) {
            this.gridAdapter.setAppItems(getAppList());
        }
    }
}