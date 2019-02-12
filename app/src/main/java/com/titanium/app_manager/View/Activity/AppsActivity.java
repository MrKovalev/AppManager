package com.titanium.app_manager.View.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;
import android.widget.TextView;

import com.titanium.app_manager.Utils.AppInfoTask;
import com.titanium.app_manager.Utils.RequestAndResultCodes;
import com.titanium.app_manager.View.Adapter.AppsAdapter;
import com.titanium.app_manager.Data.Model.AppInfo;
import com.titanium.app_manager.Interactors.AppsInteractor;
import com.titanium.app_manager.Presenter.AppsPresenter;
import com.titanium.app_manager.Presenter.IAppsPresenter;
import com.titanium.app_manager.R;
import com.titanium.app_manager.View.IAppsView;

import java.util.List;

public class AppsActivity extends AppCompatActivity
        implements IAppsView,
        AppsAdapter.ItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {


    //region Fields and Variables
    private AppsAdapter appsAdapter;
    private IAppsPresenter appsPresenter;
    private LinearLayout footer;
    private LinearLayout underToolbarSpace;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchView searchView;

    private boolean isDownloadAppsNow = true;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppMainTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            ActionBarDrawerToggle toggle =
                    new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer,
                            R.string.close_drawer);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }

        underToolbarSpace = findViewById(R.id.under_toolbar_space);
        progressBar = findViewById(R.id.progress_loading);
        recyclerView = findViewById(R.id.recyclerView);

        appsPresenter = new AppsPresenter(new AppsInteractor(this));
        appsPresenter.attachView(this);
        appsPresenter.downloadApps(new AppInfoTask(this));

        footer = findViewById(R.id.footer);
        ImageButton buttonDelete = findViewById(R.id.delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appsPresenter.deleteSelectedApps();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null){
            if (drawer.isDrawerOpen(GravityCompat.START)){
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchApp(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchApp(newText);
                return true;
            }
        });

        MenuItem itemSort = menu.findItem(R.id.action_sort);
        itemSort.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortApps();
                return true;
            }
        });

        return true;
    }

    @Override
    public void onDownloadApps(List<AppInfo> mAppsList, int currentList) {
        setAdapter();
        for (AppInfo app : mAppsList){
            appsAdapter.addApp(app);
        }

        appsAdapter.notifyDataSetChanged();
        isDownloadAppsNow = false;
    }

    @Override
    public void onUpdateList(AppInfo appInfo) {
        appsAdapter.removeApp(appInfo);
    }

    @Override
    public void onPrepareDeleteDialog(AppInfo appInfo, DialogInterface.OnClickListener negativeBtnListener,DialogInterface.OnClickListener positiveBtnListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(appInfo.getTitle())
                    .setIcon(appInfo.getAppIcon())
                    .setMessage("Do you want to uninstall this system app? \n" + "You need to reboot the device after removing this system app")
                    .setNegativeButton(R.string.prepare_dialog_no,negativeBtnListener)
                    .setPositiveButton(R.string.prepare_dialog_ok, positiveBtnListener);

            AlertDialog dialog = builder.create();
            dialog.show();
    }

    @Override
    public void onSelectionChanged(List<AppInfo> selectedListApp) {
        appsAdapter.updateSelected(selectedListApp);
        TextView countSelected = underToolbarSpace.findViewById(R.id.selected_apps);
        countSelected.setText(String.valueOf(selectedListApp.size()));
    }

    @Override
    public void onSelectionClear() {
        footer.setVisibility(View.GONE);

        if (appsAdapter != null)
            appsAdapter.clearSelected();
    }

    @Override
    public void onGoneFooter() {
        footer.setVisibility(View.GONE);
    }

    @Override
    public void onVisibleFooter() {
        footer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSetToolbarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void onRunProgressBar(Boolean switcher) {
        if (switcher){
            progressBar.setVisibility(View.VISIBLE);
        } else{
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSetUnderToolbarSpace(String allAppsTitle, String selectedAppsTitle) {
        TextView allApps = underToolbarSpace.findViewById(R.id.all_loaded_apps);
        TextView selectedApps = underToolbarSpace.findViewById(R.id.selected_apps);

        if (allApps != null)
            allApps.setText(allAppsTitle);

        if (selectedApps != null){
            selectedApps.setText(selectedAppsTitle);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_installed:
                if (!isDownloadAppsNow){
                    appsPresenter.clearSelection();
                    appsPresenter.changeList(R.string.menu_installed);
                }
                break;
            case R.id.nav_system:
                if (!isDownloadAppsNow){
                    appsPresenter.clearSelection();
                    appsPresenter.changeList(R.string.menu_system);
                    appsPresenter.checkAndPrepareRootDialog(new android.app.AlertDialog.Builder(this));
                }
                break;
            case R.id.nav_share:
                appsPresenter.shareApp();
                break;
            case R.id.nav_rate:
                appsPresenter.rateApp();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onItemClicked(AppInfo ai) {
        appsPresenter.addToSelected(ai);
    }

    @Override
    public String getStringFromResourses(int id) {
        return getString(id);
    }

    @Override
    public void onReadyActivityStart(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void onReadyActivityStartForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestAndResultCodes.REQUEST_UNINSTALL){
            if (resultCode == RequestAndResultCodes.RESULT_OK)
                appsPresenter.updateAppList();
        }
    }


    //region Local methods
    private void setAdapter(){
        appsAdapter = new AppsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appsAdapter);
    }

    private void searchApp(String query){
        if (appsAdapter != null)
            appsAdapter.filterApps(query);
    }

    private void sortApps(){
        String [] listSortVars = {"Name", "Last Modified", "Size"};
        int checkedItem = -1;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sort_title)
                .setSingleChoiceItems(listSortVars, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int checkedItem) {
                        if (appsAdapter != null)
                            appsAdapter.sortApps(checkedItem);
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion


    @Override
    protected void onDestroy() {
        appsPresenter.detachView();
        super.onDestroy();
    }
}
