package com.titanium.app_manager.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.titanium.app_manager.BuildConfig;
import com.titanium.app_manager.ui.dialog.ErrorDialogFragment;
import com.titanium.app_manager.utils.RequestAndResultCodes;
import com.titanium.app_manager.data.model.AppInfo;
import com.titanium.app_manager.domain.interactors.AppsInteractor;
import com.titanium.app_manager.presentation.AppsPresenter;
import com.titanium.app_manager.R;
import com.titanium.app_manager.presentation.view.AppsView;
import com.titanium.app_manager.utils.SystemRemoveUtils;
import com.titanium.app_manager.utils.ads.AdsShowHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppsActivity extends AppCompatActivity
        implements AppsView,
        AppsAdapter.ItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private AppsAdapter appsAdapter;
    private AppsPresenter appsPresenter;
    private FloatingActionButton deleteBtn;
    private LinearLayout underToolbarSpace;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchView searchView;
    private AdsShowHelper adsShowHelper;

    private boolean isDownloadAppsNow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppMainTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        adsShowHelper = new AdsShowHelper(getApplicationContext());

        appsPresenter = new AppsPresenter(new AppsInteractor(getApplicationContext()));
        appsPresenter.attachView(this);
    }

    private void initViews() {
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

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appsPresenter.onDeleteSelectedAppsClicked();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
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
    public void showApps(List<AppInfo> mAppsList, int currentList) {
        setAdapter();
        for (AppInfo app : mAppsList) {
            appsAdapter.addApp(app);
        }

        appsAdapter.notifyDataSetChanged();
        isDownloadAppsNow = false;
    }


    @Override
    public void updateList(AppInfo appInfo) {
        appsAdapter.removeApp(appInfo);
    }

    @Override
    public void prepareDeleteDialog(AppInfo appInfo, DialogInterface.OnClickListener negativeBtnListener, DialogInterface.OnClickListener positiveBtnListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(appInfo.getTitle())
                .setIcon(appInfo.getAppIcon())
                .setMessage(getString(R.string.dialog_system_delete))
                .setNegativeButton(R.string.prepare_dialog_no, negativeBtnListener)
                .setPositiveButton(R.string.prepare_dialog_ok, positiveBtnListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void setSelectionChanged(List<AppInfo> selectedListApp) {
        appsAdapter.updateSelected(selectedListApp);
        TextView countSelected = underToolbarSpace.findViewById(R.id.selected_apps);
        countSelected.setText(String.valueOf(selectedListApp.size()));
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setSelectionClear() {
        deleteBtn.setVisibility(View.GONE);

        if (appsAdapter != null)
            appsAdapter.clearSelected();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void hideDeleteBtn() {
        deleteBtn.setVisibility(View.GONE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showDeleteBtn() {
        deleteBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void setupToolbarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void showProgressBar(Boolean switcher) {
        if (switcher) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUnderToolbarSpace(String allAppsTitle, String selectedAppsTitle) {
        TextView allApps = underToolbarSpace.findViewById(R.id.all_loaded_apps);
        TextView selectedApps = underToolbarSpace.findViewById(R.id.selected_apps);

        if (allApps != null)
            allApps.setText(allAppsTitle);

        if (selectedApps != null) {
            selectedApps.setText(selectedAppsTitle);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_installed:
                if (!isDownloadAppsNow) {
                    appsPresenter.onSelectionCleared();
                    appsPresenter.switchAppList(R.string.menu_installed);
                }
                break;
            case R.id.nav_system:
                if (!isDownloadAppsNow) {
                    appsPresenter.onSelectionCleared();
                    appsPresenter.switchAppList(R.string.menu_system);
                    appsPresenter.checkAndPrepareRootDialog();
                }
                break;
            case R.id.nav_share:
                appsPresenter.onShareAppClicked();
                break;
            case R.id.nav_rate:
                appsPresenter.onRateAppClicked();
                break;
            case R.id.nav_privacy:
                appsPresenter.onPrivacyClicked();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void openPrivacy() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mrkovalev.github.io/Privacy"));
        startActivity(browserIntent);
    }

    @Override
    public void openMarket() {
        Intent intent = createMarketIntent("com.titanium.appmanager.prod");
        startActivity(intent);
    }

    private Intent createMarketIntent(String packageName) {
        Intent intent = getAvailableIntent("market://details?id=" + packageName);

        if (intent == null) {
            intent = getAvailableIntent("https://play.google.com/store/apps/details?id=" + packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            return intent;
        } else {
            throw new IllegalStateException();
        }
    }

    private Intent getAvailableIntent(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        List<ResolveInfo> suitableApps = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (suitableApps.size() > 0) {
            return intent;
        } else {
            return null;
        }
    }

    @Override
    public void showAds() {
        adsShowHelper.loadAd();
    }

    @Override
    public void onItemClicked(AppInfo ai) {
        appsPresenter.onItemSelected(ai);
    }

    @Override
    public String getStringFromResources(int id) {
        return getString(id);
    }

    @Override
    public void shareApp() {
        Intent intent = createShareIntent();
        startActivity(intent);
    }

    private Intent createShareIntent() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getString(R.string.share_message);
        shareBody = shareBody + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        return Intent.createChooser(sharingIntent, getString(R.string.share_title));
    }

    @Override
    public void readyActivityStartForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestAndResultCodes.REQUEST_UNINSTALL) {
            if (resultCode == RequestAndResultCodes.RESULT_OK)
                appsPresenter.updateAppList();
        }
    }

    private void setAdapter() {
        appsAdapter = new AppsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appsAdapter);
    }

    private void searchApp(String query) {
        if (appsAdapter != null)
            appsAdapter.filterApps(query);
    }

    private void sortApps() {
        String[] listSortVars = getResources().getStringArray(R.array.sort);
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

    @Override
    public void showError() {
        ErrorDialogFragment.newInstance()
                .show(getSupportFragmentManager(), "");
    }

    @Override
    public void showRootDialog() {
        AlertDialog.Builder infoDialogBuilder = new AlertDialog.Builder(this);

        infoDialogBuilder
                .setMessage(R.string.root_mess)
                .setTitle(R.string.root_title)
                .setNegativeButton(R.string.root_negative_answ, null)
                .setPositiveButton(R.string.root_positive_answ, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(SystemRemoveUtils.getRootHelpIntent());
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        appsPresenter.detachView();
        super.onDestroy();
    }
}
