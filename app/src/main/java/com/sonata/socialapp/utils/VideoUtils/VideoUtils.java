package com.sonata.socialapp.utils.VideoUtils;

import android.content.Context;
import android.util.Log;


import com.sonata.socialapp.utils.interfaces.VideoCompressListener;
import java.io.File;
import java.util.concurrent.Callable;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;



public class VideoUtils {

    private static int aa;



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



    /*public static void compressVideo(String path, String resol,long time,Context context, VideoCompressListener listener){


        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                try {
                    int rc = FFmpeg.execute("-y -i "+ path +" -vcodec libx264 -vf scale="+resol+":-2 -crf 28 -movflags +faststart -acodec copy "+ context.getCacheDir()+File.separator+"compressedvideo.mp4");
                    return rc == RETURN_CODE_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;

                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Throwable {
                        //Use result for something
                        if (!result) {
                            //hata
                            listener.OnError("error while compressing");
                        } else {
                            //success
                            listener.onSuccess(new File(context.getCacheDir()+File.separator+"compressedvideo.mp4"));
                        }

                    }
                });
        Config.enableStatisticsCallback(new StatisticsCallback() {
            public void apply(Statistics newStatistics) {
                listener.onProgress((int)((newStatistics.getTime()*100)/time));
            }
        });

    }*/




    /*public static void compressVideo(String path, String resol,long sure,Context context, VideoCompressListener listener){
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        String[] command = {"-y", "-i", path,"-vcodec", "libx264","-vf", "scale="+resol+":-2","-crf", "28","-movflags", "+faststart","-acodec","copy", contextWeakReference.get().getCacheDir()+File.separator+"video.mp4"};

        FFmpeg.getInstance(contextWeakReference.get()).execute(command, new FFcommandExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                if(contextWeakReference.get()!=null){
                    listener.onSuccess(new File(contextWeakReference.get().getCacheDir()+File.separator+"video.mp4"));
                }

            }

            @Override
            public void onProgress(String message) {
                long a = GenelUtil.getProgress(message,(int)sure/1000);
                if(a>0){
                    listener.onProgress((int)a);
                }
            }

            @Override
            public void onFailure(String message) {
                listener.OnError(message);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
        });
    }*/



}
