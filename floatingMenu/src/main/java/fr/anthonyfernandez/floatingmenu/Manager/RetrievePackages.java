package fr.anthonyfernandez.floatingmenu.Manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
//import
public class RetrievePackages {
	
	private Context _ctx;
	
	public RetrievePackages(Context ctx) {
		_ctx = ctx;
	}
	
    public ArrayList<PInfo> getPackages() {
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        for (int i=0; i<max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    public ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();        
        List<PackageInfo> packs = _ctx.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
            if ( ( (p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) != true)
            {
            	PInfo newInfo = new PInfo();
                newInfo.appname = p.applicationInfo.loadLabel(_ctx.getPackageManager()).toString();
                newInfo.pname = p.packageName;
                newInfo.versionName = p.versionName;
                newInfo.versionCode = p.versionCode;
                newInfo.icon = p.applicationInfo.loadIcon(_ctx.getPackageManager());
                res.add(newInfo);
            }
        }
        return res;
    }

//    public ArrayList<PInfo> getMostFrequentlyUsedApps() {
//
//    }

    public List<PInfo> test(List<String> filterList, int limit) {
        Intent main=new Intent(Intent.ACTION_MAIN, null);

        main.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm=_ctx.getPackageManager();

        List<ResolveInfo> launchables = pm.queryIntentActivities(main, 0);

        ArrayList<PInfo> res = new ArrayList<>(launchables.size());
        for (int i = 0; i < launchables.size(); ++i) {
            ResolveInfo app = launchables.get(i);
            boolean shouldAdd = false;
            if (filterList.size() > 0) {
                shouldAdd = filterList.contains(app.activityInfo.packageName);
            }
            else if (res.size() < limit) {
                shouldAdd = true;
            }
            if (shouldAdd) {
                PInfo pInfo = new PInfo();
                pInfo.appname = app.loadLabel(pm).toString();
                pInfo.pname = app.activityInfo.packageName;
                pInfo.icon = app.loadIcon(pm);
                res.add(pInfo);
            }
        }

        return  res;
    }

    public List<PInfo> test2(List<String> packageNames) {
        ArrayList<PInfo> res = new ArrayList<>(packageNames.size());
        for (String packageName : packageNames) {
            PInfo pInfo = getApplicationInfo(packageName);
            if (pInfo != null) {
                res.add(pInfo);
            }
        }
        return  res;
    }

    private PInfo getApplicationInfo(String packageName) {
        try {
            PackageManager pm = _ctx.getPackageManager();
            ApplicationInfo app = pm.getApplicationInfo(packageName, 0);

            Drawable icon = pm.getApplicationIcon(app);
            String name = pm.getApplicationLabel(app).toString();

            PInfo pInfo = new PInfo();
            pInfo.appname = name;
            pInfo.pname = packageName;
            pInfo.icon = icon;
            return pInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
