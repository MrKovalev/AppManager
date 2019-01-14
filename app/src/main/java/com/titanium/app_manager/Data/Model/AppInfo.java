package com.titanium.app_manager.Data.Model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppInfo implements Serializable {

    private boolean isSystem;
    private String title;
    private String packageName;
    private String sourceDir;
    private String publicSourceDir;
    private String versionName;
    private String size;
    private String dataDir,nativeLibraryDir;
    private int versionCode;
    private Drawable appIcon;

    public AppInfo(boolean isSystem, String title, String packageName, String sourceDir, String publicSourceDir, String versionName, String size, String dataDir, String nativeLibraryDir, int versionCode, Drawable icon) {
        this.isSystem = isSystem;
        this.title = title;
        this.packageName = packageName;
        this.sourceDir = sourceDir;
        this.publicSourceDir = publicSourceDir;
        this.versionName = versionName;
        this.size = size;
        this.dataDir = dataDir;
        this.nativeLibraryDir = nativeLibraryDir;
        this.versionCode = versionCode;
        this.appIcon = icon;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public String getTitle() {
        return title;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public String getPublicSourceDir() {
        return publicSourceDir;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getSize() {
        return size;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getNativeLibraryDir() {
        return nativeLibraryDir;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    @Override
    public int hashCode() {
        return (this.title.hashCode() + this.packageName.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppInfo){
            AppInfo temp = (AppInfo) obj;
            if (this.title.equals(temp.title)
                    && this.packageName.equals(temp.packageName))
                return true;
        }

        return false;
    }
}
