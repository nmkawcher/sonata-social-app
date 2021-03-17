package com.sonata.socialapp.utils.interfaces;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.List;

public interface AdListener {
    void done(List<UnifiedNativeAd> list);
}
