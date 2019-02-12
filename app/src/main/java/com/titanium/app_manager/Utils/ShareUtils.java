package com.titanium.app_manager.Utils;

import android.content.Intent;
import android.support.v4.app.ShareCompat;

import com.titanium.app_manager.BuildConfig;
import com.titanium.app_manager.R;
import com.titanium.app_manager.View.Activity.AppsActivity;

public class ShareUtils {

    public Intent shareAppAction(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Let me recommend you this application\n\n";
        shareBody = shareBody + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "App Manager");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        return Intent.createChooser(sharingIntent, "Share via");
    }
}
