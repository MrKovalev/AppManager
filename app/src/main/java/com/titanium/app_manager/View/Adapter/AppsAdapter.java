package com.titanium.app_manager.View.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.titanium.app_manager.Model.AppInfo;
import com.titanium.app_manager.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {

    private static final String TAG = "AppsAdapter";
    private ItemClickListener clickListener;
    private List<AppInfo> appList, appListCopy, selectedList;

    public interface ItemClickListener{
        void onItemClicked(AppInfo ai);
    }

    public AppsAdapter(ItemClickListener listenerClick) {
        this.appList = new ArrayList<>();
        this.appListCopy = new ArrayList<>();
        this.selectedList = new ArrayList<>();
        this.clickListener = listenerClick;
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
    public void onBindViewHolder(@NonNull final AppViewHolder appViewHolder, int i) {
        final AppInfo ai = appList.get(i);
        appViewHolder.vTitle.setText(ai.getTitle());
        appViewHolder.vPackageName.setText(ai.getPackageName());
        appViewHolder.vVersion.setText(ai.getVersionName() + " ## "+ ai.getVersionCode());
        appViewHolder.vSize.setText(ai.getSizeToShow());
        appViewHolder.vIcon.setImageDrawable(ai.getAppIcon());

        if (selectedList.contains(ai)){
            appViewHolder.v_inside_layout.setBackgroundColor(getColor(appViewHolder.vItem.getContext(), R.color.colorSelected));
            appViewHolder.checkBoxSelected.setChecked(true);
        } else{
            appViewHolder.v_inside_layout.setBackgroundColor(0);
            appViewHolder.checkBoxSelected.setChecked(false);
        }

        View.OnClickListener clickListenerItem = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedList.contains(ai)){
                    view.findViewById(R.id.layout_item).setBackgroundColor(getColor(view.getContext(), R.color.colorSelected));
                    CheckBox checkBox = view.findViewById(R.id.checkBoxSelected);
                    checkBox.setChecked(true);
                } else{
                    view.findViewById(R.id.layout_item).setBackgroundColor(0);
                    CheckBox checkBox = view.findViewById(R.id.checkBoxSelected);
                    checkBox.setChecked(false);
                }

                clickListener.onItemClicked(ai);
            }
        };

        appViewHolder.vItem.setOnClickListener(clickListenerItem);
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
        appListCopy.remove(appInfo);
        selectedList.remove(appInfo);
        notifyItemRemoved(pos);
    }

    public void updateSelected(List<AppInfo> selectedList){
        this.selectedList.clear();
        this.selectedList.addAll(selectedList);

        for (AppInfo ai : selectedList){
            int pos = appList.indexOf(ai);
            notifyItemChanged(pos);
        }
    }

    public void clearSelected(){
        this.selectedList.clear();
        notifyDataSetChanged();
    }

    private static int getColor(Context context, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return context.getColor(id);
        } else{
            return context.getResources().getColor(id);
        }
    }

    public void filterApps(String query){
        appListCopy.addAll(appList);
        appList.clear();

        if (query.isEmpty()){
            appList.addAll(appListCopy);
        } else{
            query = query.toLowerCase();
            Set<AppInfo> filterSet = new HashSet<>();
            for (AppInfo item : appListCopy){
                if (item.getPackageName().toLowerCase().contains(query)
                        || item.getTitle().toLowerCase().contains(query)){
                    filterSet.add(item);
                }
            }

            appList = new ArrayList<>(filterSet);
        }

        notifyDataSetChanged();
    }

    public void sortApps(Integer typeSort){
            switch (typeSort){
                case 0:
                    sortAppsByName();
                    break;
                case 1:
                    sortAppsByLastModified();
                    break;
                case 2:
                    sortAppsBySize();
                    break;
            }

            notifyDataSetChanged();
    }

    private void sortAppsBySize() {
        Comparator<AppInfo> sizeComparator = new Comparator<AppInfo>() {
            public int compare(AppInfo obj1, AppInfo obj2) {
                return Double.compare(obj1.getSize(), obj2.getSize());
            }
        };
        Collections.sort(appList, sizeComparator);
    }

    private void sortAppsByLastModified() {
        Comparator<AppInfo> nameComparator = new Comparator<AppInfo>() {
            public int compare(AppInfo obj1, AppInfo obj2) {

                if ((obj1.getLastModified().after(obj2.getLastModified()))){
                    return 1;
                } else if((obj1.getLastModified().before(obj2.getLastModified()))) {
                    return -1;
                } else{
                    return 0;
                }
            }
        };
        Collections.sort(appList, nameComparator);
    }

    private void sortAppsByName() {
        Comparator<AppInfo> nameComparator = new Comparator<AppInfo>() {
            public int compare(AppInfo obj1, AppInfo obj2) {
                return obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
            }
        };
        Collections.sort(appList, nameComparator);
    }


    static class AppViewHolder extends RecyclerView.ViewHolder{
        TextView vPackageName, vTitle, vVersion, vSize;
        ImageView vIcon;
        LinearLayout vItem, v_inside_layout;
        CheckBox checkBoxSelected;

        AppViewHolder(View view){
            super(view);
            vItem = view.findViewById(R.id.item);
            v_inside_layout = view.findViewById(R.id.layout_item);
            vTitle = view.findViewById(R.id.title);
            vPackageName = view.findViewById(R.id.packageName);
            vIcon = view.findViewById(R.id.icon);
            vVersion = view.findViewById(R.id.version);
            vSize = view.findViewById(R.id.size);
            checkBoxSelected = view.findViewById(R.id.checkBoxSelected);
        }
    }
}
