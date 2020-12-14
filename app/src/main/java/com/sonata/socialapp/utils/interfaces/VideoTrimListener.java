package com.sonata.socialapp.utils.interfaces;

import java.io.File;

public interface VideoTrimListener {
    void onSuccess(File file);

    void OnError(String message);

    void onProgress(int progress);
}
