package com.sonata.socialapp.utils.classes;

import android.net.Uri;

public class UploadObject {
    private Uri uri;
    private String type;

    public UploadObject(Uri uri, String type) {
        this.uri = uri;
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
