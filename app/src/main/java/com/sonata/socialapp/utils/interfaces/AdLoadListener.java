package com.sonata.socialapp.utils.interfaces;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.List;

public interface AdLoadListener {
    void onLoad(List<UnifiedNativeAd> unifiedNativeAdList);
    void onFail(int errorCode);
}
