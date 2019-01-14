package com.titanium.app_manager.Interactors;

import com.titanium.app_manager.Data.Model.AppInfo;

import java.util.List;

public interface IInteractor {
    List<AppInfo> filterApps(int currentList, List<AppInfo> mAppsList);
    void sortAppsList(List<AppInfo> mAppsList);
}
