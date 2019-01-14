package com.titanium.app_manager.Interactors;

import android.content.Context;

import com.titanium.app_manager.Data.Model.AppInfo;
import com.titanium.app_manager.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        sortAppsList(appList);

        return appList;
    }

    @Override
    public void sortAppsList(List<AppInfo> mAppsList) {
        Comparator<AppInfo> myComparator = new Comparator<AppInfo>() {
            public int compare(AppInfo obj1, AppInfo obj2) {
                return obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
            }
        };

        Collections.sort(mAppList, myComparator);
    }

}
