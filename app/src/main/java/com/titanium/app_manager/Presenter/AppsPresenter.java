package com.titanium.app_manager.Presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.titanium.app_manager.Model.AppInfo;
import com.titanium.app_manager.Interactors.IInteractor;
import com.titanium.app_manager.R;
import com.titanium.app_manager.Utils.AppInfoTask;
import com.titanium.app_manager.Utils.RateUtils;
import com.titanium.app_manager.Utils.RequestAndResultCodes;
import com.titanium.app_manager.Utils.ShareUtils;
import com.titanium.app_manager.Utils.SystemRemoveUtils;
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
        iAppsView.onSetToolbarTitle(iAppsView.getStringFromResourses(R.string.loading_text));
        iAppsView.onRunProgressBar(true);

        mAppInfoTask.setAsyncResponce(new AppInfoTask.AsyncResponse() {
            @Override
            public void onTaskComplete(List<AppInfo> result) {
                mAppsList = result;
                initAppsAdapter();
            }
        });

        mAppInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //предназначен для работы только с пользовательскими приложениями
    @Override
    public void updateAppList() {
        for (AppInfo appInfo : mSelectedList){
            if (!appInfo.isSystem()){
                iAppsView.onUpdateList(appInfo);
            }
        }

        List<AppInfo> appList = iInteractor.filterApps(currentList, mAppsList);
        String allAppsMes = iAppsView.getStringFromResourses(currentList) + " ("+ appList.size()+ ")";
        iAppsView.onSetUnderToolbarSpace(allAppsMes, "0");
        clearSelection();
    }

    @Override
    public void changeList(int change) {
        currentList = change;
        initAppsAdapter();
    }

    @Override
    public void addToSelected(AppInfo ai) {
        if (!mSelectedList.contains(ai)){
            mSelectedList.add(ai);
        } else{
            mSelectedList.remove(ai);
        }

        iAppsView.onSelectionChanged(mSelectedList);

        if (!mSelectedList.isEmpty()){
            iAppsView.onVisibleFooter();
        } else{
            iAppsView.onGoneFooter();
        }
    }

    @Override
    public void clearSelection() {
        mSelectedList.clear();
        iAppsView.onSelectionClear();
    }

    @Override
    public void deleteSelectedApps() {
        for (final AppInfo appInfo : mSelectedList){
            if (currentList == R.string.menu_installed) {
                Uri packageURI = Uri.parse("package:" + appInfo.getPackageName());
                Intent uninstallAppIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                uninstallAppIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);

                iAppsView.onReadyActivityStartForResult(uninstallAppIntent, RequestAndResultCodes.REQUEST_UNINSTALL);
                mAppsList.remove(appInfo);
            } else {
                iAppsView.onPrepareDeleteDialog(appInfo, null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SystemRemoveUtils.removeSystemApp(appInfo.getPublicSourceDir());
                    }
                });
            }
        }
    }

    @Override
    public void checkAndPrepareRootDialog(AlertDialog.Builder infoDialogBuilder) {
        if (!SystemRemoveUtils.checkRootPermission()){
            infoDialogBuilder
                    .setMessage(R.string.root_mess)
                    .setTitle(R.string.root_title)
                    .setNegativeButton(R.string.root_negative_answ,null)
                    .setPositiveButton(R.string.root_positive_answ, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            iAppsView.onReadyActivityStart(SystemRemoveUtils.getRootHelpIntent());
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void shareApp() {
        ShareUtils shareUtils = new ShareUtils();
        iAppsView.onReadyActivityStart(shareUtils.shareAppAction());
    }

    @Override
    public void rateApp() {
        iAppsView.onReadyActivityStart(new RateUtils().findApplicationsForRate());
    }

    private void initAppsAdapter() {
        List<AppInfo> appList = iInteractor.filterApps(currentList, mAppsList);
        iAppsView.onRunProgressBar(false);
        iAppsView.onSetToolbarTitle(iAppsView.getStringFromResourses(R.string.home));

        String allAppsMes = iAppsView.getStringFromResourses(currentList) + " ("+ appList.size()+ ")";
        iAppsView.onSetUnderToolbarSpace(allAppsMes, String.valueOf(mSelectedList.size()));
        iAppsView.onDownloadApps(appList, currentList);
    }
}
