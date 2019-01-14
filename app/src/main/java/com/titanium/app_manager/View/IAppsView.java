package com.titanium.app_manager.View;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import com.titanium.app_manager.Data.Model.AppInfo;

import java.util.List;

public interface IAppsView {
    void onDownloadApps(List<AppInfo> mAppsList, int currentList);
}
