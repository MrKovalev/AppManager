package com.titanium.app_manager.Utils.AppsDownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import com.titanium.app_manager.Data.Model.AppInfo;

import java.io.File;
import java.util.ArrayList;
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
    protected List<AppInfo> doInBackground(Void... voids) {
        final PackageManager pm = context.getPackageManager();
        List<AppInfo> apps = new ArrayList<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages){
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(applicationInfo.packageName,0);

                File file = new File(applicationInfo.publicSourceDir);
                String size = formateFileSize(context, file.length());
                Drawable icon = pm.getApplicationIcon(applicationInfo.packageName);

                apps.add(new AppInfo(isSystemPackage(packageInfo)
                        ,applicationLabel(context, applicationInfo)
                        ,applicationInfo.packageName
                        ,applicationInfo.sourceDir
                        ,applicationInfo.publicSourceDir
                        ,packageInfo.versionName
                        ,size
                        ,applicationInfo.dataDir
                        ,applicationInfo.nativeLibraryDir
                        ,packageInfo.versionCode
                        ,icon));

                Log.d("LOAD", "add el");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return apps;
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
