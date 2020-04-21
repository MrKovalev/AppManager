package com.titanium.app_manager.presentation.view;

import android.content.DialogInterface;
import android.content.Intent;

import com.titanium.app_manager.base.BaseView;
import com.titanium.app_manager.data.model.AppInfo;

import java.util.List;

public interface AppsView extends BaseView {
    void showApps(List<AppInfo> mAppsList, int currentList);
    void updateList(AppInfo appInfo);

    void setupToolbarTitle(String title);
    void setUnderToolbarSpace(String allAppsTitle, String selectedAppsTitle);
    void showProgressBar(Boolean switcher);

    void hideDeleteBtn();
    void showDeleteBtn();
    void prepareDeleteDialog(AppInfo selectedApp, DialogInterface.OnClickListener negativeBtnListener, DialogInterface.OnClickListener positiveBtnListener);
    void setSelectionChanged(List<AppInfo> selectedListApp);
    void setSelectionClear();

    void shareApp();
    void openMarket();
    void openPrivacy();
    void readyActivityStartForResult(Intent intent, int requestCode);

    String getStringFromResources(int id);

    void showError();
    void showRootDialog();

    void showAds();
}
