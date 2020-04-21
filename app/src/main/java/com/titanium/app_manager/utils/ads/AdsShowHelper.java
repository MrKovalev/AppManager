package com.titanium.app_manager.utils.ads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdsShowHelper {

    private static final String adsID = "ca-app-pub-9743454109151384/5228467023";
    //private static final String adsID = "ca-app-pub-3940256099942544/1033173712";

    private final Context context;
    private InterstitialAd interstitialAd;

    public AdsShowHelper(Context context) {
        this.context = context;

        prepareBanner();
    }

    private void prepareBanner() {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(adsID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void loadAd() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.d("TAG", "NOT LOADED YET");
        }

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                Log.d("TAG", "CLIKED");
            }

            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}
