package com.titanium.app_manager.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.titanium.app_manager.base.BasePresenter;
import com.titanium.app_manager.data.model.AppInfo;
import com.titanium.app_manager.R;
import com.titanium.app_manager.domain.interactors.AppsInteractor;
import com.titanium.app_manager.utils.RequestAndResultCodes;
import com.titanium.app_manager.utils.SystemRemoveUtils;
import com.titanium.app_manager.presentation.view.AppsView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AppsPresenter extends BasePresenter<AppsView> {

    private AppsInteractor appsInteractor;
    private List<AppInfo> appInfoList, selectedList;
    private int currentList = R.string.menu_installed;

    public AppsPresenter(AppsInteractor interactor) {
        this.appsInteractor = interactor;
        appInfoList = new ArrayList<>();
        selectedList = new ArrayList<>();
    }

    @Override
    protected void onViewAttached() {
        super.onViewAttached();

        initApps();
    }

    private void initApps() {
        view.setupToolbarTitle(view.getStringFromResources(R.string.loading_text));
        view.showProgressBar(true);

        Disposable disposable = appsInteractor.loadApps()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<List<AppInfo>>() {
                            @Override
                            public void accept(List<AppInfo> appInfos) {
                                onAppsLoaded(appInfos);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                handleError();
                            }
                        }
                );

        disposeOnDestroy(disposable);
    }

    private void onAppsLoaded(List<AppInfo> apps) {
        appInfoList = apps;
        initAppsAdapter();
    }

    private void handleError() {
        view.showError();
    }

    public void updateAppList() {
        for (AppInfo appInfo : selectedList) {
            if (!appInfo.isSystem()) {
                view.updateList(appInfo);
            }
        }

        List<AppInfo> appList = appsInteractor.filterApps(currentList, appInfoList);
        String allAppsMes = view.getStringFromResources(currentList) + " (" + appList.size() + ")";
        view.setUnderToolbarSpace(allAppsMes, "0");
        onSelectionCleared();
    }

    public void switchAppList(int change) {
        appsInteractor.addAdsCounter();

        currentList = change;
        initAppsAdapter();

        if (isNeedToShowAds())
            showAds();
    }

    public void onItemSelected(AppInfo app) {
        addToSelected(app);

        if (app.isSystem()) {
            prepareSystemAppForDelete(app);
        } else {
            prepareUserAppForDelete(app);
        }
    }

    private void prepareSystemAppForDelete(AppInfo app) {
        if (SystemRemoveUtils.isRootGranted()) {
            if (!selectedList.isEmpty()) {
                view.showDeleteBtn();
            } else {
                view.hideDeleteBtn();
            }
        } else {
            view.hideDeleteBtn();
            view.showRootDialog();
        }
    }

    private void prepareUserAppForDelete(AppInfo app) {
        if (!selectedList.isEmpty()) {
            view.showDeleteBtn();
        } else {
            view.hideDeleteBtn();
        }
    }

    private void addToSelected(AppInfo app) {
        if (!selectedList.contains(app)) {
            selectedList.add(app);
        } else {
            selectedList.remove(app);
        }

        view.setSelectionChanged(selectedList);
    }

    public void onSelectionCleared() {
        selectedList.clear();
        view.setSelectionClear();
    }

    public void onDeleteSelectedAppsClicked() {
        if (isNeedToShowAds()) {
            showAds();
        } else {
            deleteSelected();
        }
    }

    private boolean isNeedToShowAds() {
        return appsInteractor.getAdsCounter() > 2;
    }

    private void showAds() {
        view.showAds();
        appsInteractor.clearAdsCounter();
    }

    private void deleteSelected() {
        for (final AppInfo appInfo : selectedList) {
            if (currentList == R.string.menu_installed) {
                Uri packageURI = Uri.parse("package:" + appInfo.getPackageName());
                Intent uninstallAppIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                uninstallAppIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);

                view.readyActivityStartForResult(uninstallAppIntent, RequestAndResultCodes.REQUEST_UNINSTALL);
                appInfoList.remove(appInfo);

                appsInteractor.addAdsCounter();
            } else {
                view.prepareDeleteDialog(appInfo, null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SystemRemoveUtils.removeSystemApp(appInfo.getPublicSourceDir());
                    }
                });

                appsInteractor.addAdsCounter();
            }
        }
    }

    public void checkAndPrepareRootDialog() {
        if (!SystemRemoveUtils.isRootGranted()) {
            view.showRootDialog();
        }
    }

    public void onShareAppClicked() {
        view.shareApp();
    }

    public void onRateAppClicked() {
        view.openMarket();
    }

    public void onPrivacyClicked() {
        view.openPrivacy();
    }

    private void initAppsAdapter() {
        List<AppInfo> appList = appsInteractor.filterApps(currentList, appInfoList);
        view.showProgressBar(false);
        view.setupToolbarTitle(view.getStringFromResources(R.string.home));

        String allAppsMes = view.getStringFromResources(currentList) + " (" + appList.size() + ")";
        view.setUnderToolbarSpace(allAppsMes, String.valueOf(selectedList.size()));
        view.showApps(appList, currentList);
    }
}
