package com.titanium.app_manager.View.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;

import com.titanium.app_manager.Utils.AppsDownloader.AppInfoTask;
import com.titanium.app_manager.View.Adapter.AppsAdapter;
import com.titanium.app_manager.Data.Model.AppInfo;
import com.titanium.app_manager.Interactors.AppsInteractor;
import com.titanium.app_manager.Presenter.AppsPresenter;
import com.titanium.app_manager.Presenter.IAppsPresenter;
import com.titanium.app_manager.R;
import com.titanium.app_manager.View.IAppsView;

import java.util.List;

public class AppsActivity extends AppCompatActivity implements IAppsView,
        NavigationView.OnNavigationItemSelectedListener {

    private AppsAdapter appsAdapter;
    private IAppsPresenter appsPresenter;
    private LinearLayout footer;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_spinner);
        recyclerView = findViewById(R.id.recyclerView);

        appsPresenter = new AppsPresenter(new AppsInteractor(this));
        appsPresenter.attachView(this);
        appsPresenter.downloadApps(new AppInfoTask(this));

        footer = findViewById(R.id.footer);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onDestroy() {
        appsPresenter.detachView();
        super.onDestroy();
    }


    @Override
    public void onDownloadApps(List<AppInfo> mAppsList, int currentList) {
        setAdapter();
        for (AppInfo app : mAppsList){
            appsAdapter.addApp(app);
        }

        appsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_installed:
                appsPresenter.changeList(R.string.menu_installed);
                break;
            case R.id.nav_system:
                appsPresenter.changeList(R.string.menu_system);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void setAdapter(){
        appsAdapter = new AppsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(appsAdapter);
    }
}
