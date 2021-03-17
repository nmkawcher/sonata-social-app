package com.sonata.socialapp.utils.VideoUtils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.interfaces.VideoCompressListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.google.protobuf.CodedOutputStream.DEFAULT_BUFFER_SIZE;


public class VideoUtils {

    private static int aa;
    static List<String> urlList = null;

    public static List<String> getUrlList(){
        if(urlList == null){
            urlList = new ArrayList<>();
        }
        return urlList;
    }



    public static void compressVideo(String path, String resol,long time,Context context, VideoCompressListener listener){
        aa = 0;
        EpEditor.execCmd("-y -i "+ path +" -vcodec libx264 -vf scale="+resol+":-2 -crf 28 -movflags +faststart -acodec copy "+ context.getCacheDir()+File.separator+"compressedvideo.mp4"
                , time*1000
                , new OnEditorListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(new File(context.getCacheDir()+File.separator+"compressedvideo.mp4"));
            }

            @Override
            public void onFailure() {
                listener.OnError("error while compressing");
            }

            @Override
            public void onProgress(float progress) {

                try{
                    if(progress == 1.0f){
                        listener.onProgress(100);
                    }
                    else{
                        String s = String.valueOf(progress);
                        String sub = s.substring(s.lastIndexOf(".")+1).substring(0,2);
                        Log.e("Sub String",sub);

                        int a = Integer.parseInt(sub);

                        if(a>aa){
                            listener.onProgress(a);
                        }
                        aa = a;
                    }


                }
                catch (Exception ignored){

                }
                Log.e("progress",progress+"");
            }
        });
    }


    public static void preLoadVideo(String url2, Context context, int sira){

    }


}
