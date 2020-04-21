package com.titanium.app_manager.domain.interactors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;

import com.titanium.app_manager.data.model.AppInfo;
import com.titanium.app_manager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;

public class AppsInteractor {

    private Context context;
    private int adsCounter = 0;

    public AppsInteractor(Context context) {
        this.context = context;
    }

    public List<AppInfo> filterApps(int currentList, List<AppInfo> mAppsList) {
        List<AppInfo> appList = new ArrayList<>();
        if (currentList == R.string.menu_installed){
            for (AppInfo appInfo : mAppsList){
                if (!appInfo.isSystem()){
                    appList.add(appInfo);
                }
            }
        } else{
            for (AppInfo appInfo : mAppsList){
                if (appInfo.isSystem()){
                    appList.add(appInfo);
                }
            }
        }

        return appList;
    }

    public void addAdsCounter() {
        adsCounter++;
    }

    public int getAdsCounter() {
        return adsCounter;
    }

    public void clearAdsCounter() {
        adsCounter = 0;
    }

    public Single<List<AppInfo>> loadApps() {
        return Single.fromCallable(new Callable<List<AppInfo>>() {
            @Override
            public List<AppInfo> call() throws Exception {
                List<AppInfo> apps = loadAppsInfo();
                return apps;
            }
        });
    }

    private List<AppInfo> loadAppsInfo() {
        final PackageManager pm = context.getPackageManager();

        List<AppInfo> apps = new ArrayList<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages){
            PackageInfo packageInfo;

            try {
                packageInfo = pm.getPackageInfo(applicationInfo.packageName,0);

                File file = new File(applicationInfo.publicSourceDir);
                long size = file.length();
                Drawable icon = pm.getApplicationIcon(applicationInfo.packageName);
                Date lastModified = new Date(packageInfo.lastUpdateTime);
                int version = 0;

                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    version = (int) packageInfo.getLongVersionCode();
                } else {
                    version = packageInfo.versionCode;
                }

                apps.add(
                        new AppInfo(isSystemPackage(packageInfo)
                                ,applicationLabel(context, applicationInfo)
                                ,applicationInfo.packageName
                                ,applicationInfo.sourceDir
                                ,applicationInfo.publicSourceDir
                                ,packageInfo.versionName
                                ,size
                                ,formateFileSize(context, size)
                                ,applicationInfo.dataDir
                                ,applicationInfo.nativeLibraryDir
                                ,version
                                ,icon
                                ,lastModified)
                );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return apps;
    }

    private String formateFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private String applicationLabel(Context con, ApplicationInfo packageInfo) {
        PackageManager p = con.getPackageManager();
        return p.getApplicationLabel(packageInfo).toString();
    }
}
