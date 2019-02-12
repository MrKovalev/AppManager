package com.titanium.app_manager.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import com.titanium.app_manager.Data.Model.AppInfo;

import java.util.List;

public interface IAppsView {
    void onDownloadApps(List<AppInfo> mAppsList, int currentList);
    void onUpdateList(AppInfo appInfo);

    void onSetToolbarTitle(String title);
    void onSetUnderToolbarSpace(String allAppsTitle, String selectedAppsTitle);
    void onRunProgressBar(Boolean switcher);

    void onGoneFooter();
    void onVisibleFooter();
    void onPrepareDeleteDialog(AppInfo selectedApp, DialogInterface.OnClickListener negativeBtnListener, DialogInterface.OnClickListener positiveBtnListener);
    void onSelectionChanged(List<AppInfo> selectedListApp);
    void onSelectionClear();

    void onReadyActivityStart(Intent intent);
    void onReadyActivityStartForResult(Intent intent, int requestCode);

    String getStringFromResourses(int id);
}
