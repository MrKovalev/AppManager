package com.titanium.app_manager.Presenter;

import android.os.AsyncTask;

import com.titanium.app_manager.Data.Model.AppInfo;
import com.titanium.app_manager.Interactors.IInteractor;
import com.titanium.app_manager.R;
import com.titanium.app_manager.Utils.AppsDownloader.AppInfoTask;
import com.titanium.app_manager.View.IAppsView;

import java.util.ArrayList;
import java.util.List;

public class AppsPresenter implements IAppsPresenter {

    private IAppsView iAppsView;
    private IInteractor iInteractor;
    private List<AppInfo> mAppsList, mSelectedList;
    private int currentList = R.string.menu_installed;

    public AppsPresenter(IInteractor interactor) {
        this.iInteractor = interactor;
        mAppsList = new ArrayList<>();
        mSelectedList = new ArrayList<>();
    }

    @Override
    public void detachView() {
        iAppsView = null;
    }

    @Override
    public void attachView(IAppsView mainView) {
        this.iAppsView = mainView;
    }

    @Override
    public void downloadApps(AppInfoTask mAppInfoTask) {
        mAppInfoTask.setAsyncResponce(new AppInfoTask.AsyncResponse() {
            @Override
            public void onTaskComplete(List<AppInfo> result) {
                mAppsList = result;
                initAppsAdapter();
            }
        });

        mAppInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void changeList(int change) {
        currentList = change;
        initAppsAdapter();
    }

    private void initAppsAdapter() {
        List<AppInfo> appList = iInteractor.filterApps(currentList, mAppsList);
        iAppsView.onDownloadApps(appList, currentList);
    }
}
