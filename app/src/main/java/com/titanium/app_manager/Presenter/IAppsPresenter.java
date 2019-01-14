package com.titanium.app_manager.Presenter;

import com.titanium.app_manager.Utils.AppsDownloader.AppInfoTask;
import com.titanium.app_manager.View.IAppsView;

public interface IAppsPresenter {
    void detachView();
    void attachView(IAppsView mainView);

    void downloadApps(AppInfoTask mAppInfoTask);
    void changeList(int change);
}
