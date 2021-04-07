package com.sonata.socialapp.utils.interfaces;

import java.io.File;

public interface FileCompressListener {
    void done(File file);
    void error(String message);
}
