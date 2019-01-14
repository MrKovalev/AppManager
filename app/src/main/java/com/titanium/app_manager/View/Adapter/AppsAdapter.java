package com.titanium.app_manager.View.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.titanium.app_manager.Data.Model.AppInfo;
import com.titanium.app_manager.R;

import java.util.ArrayList;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {

    private static final String TAG = "AppsAdapter";
    private ItemClickListener clickListener;
    private List<AppInfo> appList, appListCopy, selectedList;

    public interface ItemClickListener{
        void onItemClicked(AppInfo ai);
    }

    public AppsAdapter() {
        this.appList = new ArrayList<>();
        this.appListCopy = new ArrayList<>();
        this.selectedList = new ArrayList<>();
       // this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.app_item_layout, viewGroup, false);
        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder appViewHolder, int i) {
        final AppInfo ai = appList.get(i);
        appViewHolder.vTitle.setText(ai.getTitle());
        appViewHolder.vPackageName.setText(ai.getPackageName());
        appViewHolder.vVersion.setText(ai.getVersionName() + " ## "+ ai.getVersionCode());
        appViewHolder.vSize.setText(ai.getSize());
        appViewHolder.vIcon.setImageDrawable(ai.getAppIcon());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public void addApp(AppInfo appInfo){
        appList.add(appInfo);
    }

    public void removeApp(AppInfo appInfo){
        int pos = appList.indexOf(appInfo);
        appList.remove(appInfo);
        notifyItemRemoved(pos);
    }


    public static class AppViewHolder extends RecyclerView.ViewHolder{
        TextView vPackageName, vTitle, vVersion, vSize;
        ImageView vIcon;
        LinearLayout vItem;

        public AppViewHolder(View view){
            super(view);
            vItem = view.findViewById(R.id.item);
            vTitle = view.findViewById(R.id.title);
            vPackageName = view.findViewById(R.id.packageName);
            vIcon = view.findViewById(R.id.icon);
            vVersion = view.findViewById(R.id.version);
            vSize = view.findViewById(R.id.size);
        }
    }
}
