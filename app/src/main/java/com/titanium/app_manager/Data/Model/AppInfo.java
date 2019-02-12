package com.titanium.app_manager.Data.Model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Date;

public class AppInfo implements Serializable {

    private boolean isSystem;
    private String title;
    private String packageName;
    private String sourceDir;
    private String publicSourceDir;
    private String versionName;
    private long size;
    private String dataDir,nativeLibraryDir, sizeToShow;
    private int versionCode;
    private Drawable appIcon;
    private Date lastModified;

    public AppInfo(boolean isSystem, String title, String packageName, String sourceDir, String publicSourceDir, String versionName, long size, String sizeToShow, String dataDir, String nativeLibraryDir, int versionCode, Drawable icon, Date lastModified) {
        this.isSystem = isSystem;
        this.title = title;
        this.packageName = packageName;
        this.sourceDir = sourceDir;
        this.publicSourceDir = publicSourceDir;
        this.versionName = versionName;
        this.size = size;
        this.sizeToShow = sizeToShow;
        this.dataDir = dataDir;
        this.nativeLibraryDir = nativeLibraryDir;
        this.versionCode = versionCode;
        this.appIcon = icon;
        this.lastModified = lastModified;
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

    public long getSize() {
        return size;
    }

    public String getSizeToShow() {
        return sizeToShow;
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

    public Date getLastModified() { return lastModified; }

    @Override
    public int hashCode() {
        return (this.title.hashCode() + this.packageName.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppInfo){
            AppInfo temp = (AppInfo) obj;
            return this.title.equals(temp.title)
                    && this.packageName.equals(temp.packageName);
        }

        return false;
    }
}
