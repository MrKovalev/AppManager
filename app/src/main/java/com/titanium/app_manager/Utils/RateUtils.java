package com.titanium.app_manager.Utils;

import android.content.Intent;
import android.net.Uri;

public class RateUtils {

    public Intent findApplicationsForRate(){
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:UsefulAndroidApps"));
    }
}
