package com.titanium.app_manager.Interactors;

import android.content.Context;

import com.titanium.app_manager.Model.AppInfo;
import com.titanium.app_manager.R;

import java.util.ArrayList;
import java.util.List;

public class AppsInteractor implements IInteractor {

    private Context context;
    private List<AppInfo> mAppList = new ArrayList<>();

    public AppsInteractor(Context context) {
        this.context = context;
    }

    @Override
    public List<AppInfo> filterApps(int currentList, List<AppInfo> mAppsList) {
        List<AppInfo> appList = new ArrayList<>();
        if (currentList == R.string.menu_installed){
            for (AppInfo appInfo : mAppsList){
                if (!appInfo.isSystem()){
                    appList.add(appInfo);
                }
            }
        } else{
            for (AppInfo appInfo : mAppsList){
                if (appInfo.isSystem()){
                    appList.add(appInfo);
                }
            }
        }

        return appList;
    }
}
