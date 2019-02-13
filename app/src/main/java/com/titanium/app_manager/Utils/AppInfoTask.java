package com.titanium.app_manager.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.format.Formatter;

import com.titanium.app_manager.Model.AppInfo;

import java.io.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppInfoTask extends AsyncTask<Void, Void, List<AppInfo>> {

    public interface AsyncResponse {
        void onTaskComplete(List<AppInfo> result);
    }

    private static final String TAG = "AppInfoTask";
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private AsyncResponse delegate = null;

    public AppInfoTask(Context context){
        this.context = context;
    }

    public void setAsyncResponce(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected List<AppInfo> doInBackground(Void... params) {
        final PackageManager pm = context.getPackageManager();
        List<AppInfo> appList = new ArrayList<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages){
            PackageInfo packageInfo = null;
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

                appList.add(new AppInfo(isSystemPackage(packageInfo)
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
                        ,lastModified));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return appList;
    }

    @Override
    protected void onPostExecute(List<AppInfo> appInfos) {
        super.onPostExecute(appInfos);
        if (appInfos != null && delegate != null){
            delegate.onTaskComplete(appInfos);
        }
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
