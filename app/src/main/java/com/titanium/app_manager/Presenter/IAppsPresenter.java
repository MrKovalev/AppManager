package com.titanium.app_manager.Presenter;

import android.app.AlertDialog;
import android.support.v4.app.ShareCompat;

import com.titanium.app_manager.Data.Model.AppInfo;
import com.titanium.app_manager.Utils.AppInfoTask;
import com.titanium.app_manager.View.IAppsView;

public interface IAppsPresenter {
    void detachView();
    void attachView(IAppsView mainView);

    void downloadApps(AppInfoTask mAppInfoTaskSys);
    void updateAppList();
    void changeList(int change);
    void addToSelected(AppInfo ai);
    void clearSelection();
    void deleteSelectedApps();
    void checkAndPrepareRootDialog(AlertDialog.Builder infoDialogBuilder);

    void shareApp();
    void rateApp();
}
