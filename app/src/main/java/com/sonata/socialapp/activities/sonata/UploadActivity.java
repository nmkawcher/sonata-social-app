package com.sonata.socialapp.activities.sonata;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;



import com.bumptech.glide.Glide;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.socialview.Mention;
import com.sonata.socialapp.socialview.SocialAutoCompleteTextView;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.VideoUtils.VideoCompress;
import com.sonata.socialapp.utils.VideoUtils.VideoUtils;
import com.sonata.socialapp.utils.adapters.MentionAdapter;
import com.sonata.socialapp.utils.adapters.UploadPostAdapter;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.classes.UploadObject;
import com.sonata.socialapp.utils.interfaces.UploadPostClick;
import com.sonata.socialapp.utils.interfaces.VideoCompressListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.ui.MatisseActivity;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class UploadActivity extends AppCompatActivity implements UploadPostClick {

    Uri uri;
    List<UploadObject> list;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    UploadPostAdapter adapter;
    private boolean loading=true;
    private long mLastClickTime = 0;
    private final int imagerequestcode = 13271;
    private final int videorequestcode = 24381;
    RelativeLayout uploadsec;
    SocialAutoCompleteTextView postdesc;
    RelativeLayout addphoto,addvideo,backbutton,uploadbutton;
    ProgressDialog progressDialog;

    private ArrayAdapter<Mention> defaultMentionAdapter;


    @Override
    protected void onDestroy() {
        postdesc = null;
        uri=null;
        uploadsec=null;
        addphoto = null;
        addvideo = null;
        backbutton = null;
        uploadbutton = null;
        progressDialog.dismiss();
        progressDialog = null;
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        backbutton=findViewById(R.id.backbuttonbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onBackPressed();

            }

        });
        progressDialog= new ProgressDialog(this);
        progressDialog.setCancelable(false);

        recyclerView=findViewById(R.id.uploadRecyclerView);

        uploadsec=findViewById(R.id.uploadmedialayout);
        addphoto=findViewById(R.id.uploadaddphoto);
        addvideo=findViewById(R.id.uploadaddvideo);
        postdesc=findViewById(R.id.postdesc);

        String text = String.format(getResources().getString(R.string.upload_hint), "@"+ParseUser.getCurrentUser().getUsername());
        postdesc.setHint(text);
        postdesc.setMentionPattern(Pattern.compile("(^|[^\\w])@([\\w\\_\\.]+)"));
        postdesc.setHashtagColor(getResources().getColor(R.color.blue));
        postdesc.setMentionColor(getResources().getColor(R.color.blue));
        postdesc.setHyperlinkColor(getResources().getColor(R.color.blue));

        defaultMentionAdapter = new MentionAdapter(UploadActivity.this);
        postdesc.setMentionAdapter(defaultMentionAdapter);

        postdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(count > before){
                    if(s.charAt(start) == ' ' || s.charAt(start) == '\n') return;

                    final String text = s.toString();

                    if(start > 0){


                        int spacePosition = -1;
                        int cursorPosition = start + count -1;
                        if(cursorPosition < 0) return;
                        while (true) {
                            if(cursorPosition < 0) {
                                break;
                            }
                            else {
                                if(s.charAt(cursorPosition) == ' ' || s.charAt(cursorPosition) == '\n'){
                                    spacePosition = cursorPosition + 1;
                                    break;
                                }
                                else {
                                    cursorPosition -=1;
                                }
                            }

                        }
                        if(spacePosition < 0) return;
                        if((start + count) - spacePosition < 1) return;
                        final String queryString = text.substring(spacePosition,start + count);
                        Log.e("Query String",queryString);
                        final String t = s.toString();
                        if(queryString.startsWith("@")&&queryString.replace("@","").length()>0){
                            //Do Query


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    if(GenelUtil.isAlive(UploadActivity.this)){
                                        if(t.equals(postdesc.getText().toString())){
                                            HashMap<String, Object> params = new HashMap<>();
                                            params.put("text",queryString.replace("@","").toLowerCase());
                                            ParseCloud.callFunctionInBackground("searchPerson", params, (FunctionCallback<List<SonataUser>>) (object, e) -> {
                                                Log.e("done","done");
                                                if(GenelUtil.isAlive(UploadActivity.this)){
                                                    if(e==null){
                                                        if(t.equals(postdesc.getText().toString())){
                                                            if(object!= null){
                                                                defaultMentionAdapter.clear();
                                                                for(int i = 0; i < object.size(); i++){
                                                                    if(object.get(i).getHasPp()){
                                                                        defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                                ,object.get(i).getName(),object.get(i).getPPAdapter()));
                                                                    }
                                                                    else{
                                                                        defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                                ,object.get(i).getName(),"empty"));
                                                                    }

                                                                }
                                                                defaultMentionAdapter.notifyDataSetChanged();

                                                                //initList(objects);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }

                                }
                            }, 1000);
                        }


                    }


                }
                else{
                    defaultMentionAdapter.clear();
                    defaultMentionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().contains(" ")){
                    if(s.toString().startsWith("@")){
                        final String t = s.toString();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if(GenelUtil.isAlive(UploadActivity.this)){
                                    if(t.equals(postdesc.getText().toString())){
                                        HashMap<String, Object> params = new HashMap<>();
                                        params.put("text",t.replace("@","").toLowerCase());
                                        ParseCloud.callFunctionInBackground("searchPerson", params, (FunctionCallback<List<SonataUser>>) (object, e) -> {
                                            Log.e("done","done");
                                            if(GenelUtil.isAlive(UploadActivity.this)){
                                                if(e==null){
                                                    if(t.equals(postdesc.getText().toString())){
                                                        if(object!= null){
                                                            defaultMentionAdapter.clear();
                                                            for(int i = 0; i < object.size(); i++){
                                                                if(object.get(i).getHasPp()){
                                                                    defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                            ,object.get(i).getName(),object.get(i).getPPAdapter()));
                                                                }
                                                                else{
                                                                    defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                            ,object.get(i).getName(),"empty"));
                                                                }

                                                            }
                                                            defaultMentionAdapter.notifyDataSetChanged();

                                                            //initList(objects);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                            }
                        }, 1000);
                    } else {
                        defaultMentionAdapter.clear();
                        defaultMentionAdapter.notifyDataSetChanged();
                    }
                } else {
                    defaultMentionAdapter.clear();
                    defaultMentionAdapter.notifyDataSetChanged();
                }

            }
        });



        backbutton=findViewById(R.id.backbuttonbutton);
        uploadbutton=findViewById(R.id.uploadtextripple);





        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                Permissions.check(UploadActivity.this,permissions,null, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        selectImage();
                        /*Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,getString(R.string.photo)),imagerequestcode);*/
                    }
                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions){

                    }
                });

            }
        });



        addvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                Permissions.check(UploadActivity.this,permissions,null, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        /*Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setType("video/*");
                        startActivityForResult(Intent.createChooser(intent,getString(R.string.video)),videorequestcode);*/
                        selectVideo();
                    }
                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions){

                    }
                });

            }
        });



        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                GenelUtil.hideKeyboard(UploadActivity.this);
                if(list.size()>0){
                    if(list.get(0).getType().equals("image")){
                        progressDialog.show();
                        progressDialog.setMessage(getString(R.string.preparingimage));

                        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                        options.size=55;

                        List<Uri> uriList = new ArrayList<>();
                        for(int i = 0;i<list.size();i++){
                            if(!list.get(i).getType().equals("add")){
                                uriList.add(list.get(i).getUri());
                            }
                        }
                        compressImages(uriList,0,new ArrayList<File>(),options);


                    }
                    else{
                        if(list.get(0).getType().equals("video")){
                            progressDialog.show();
                            //anan
                            loadVideoNormally(list.get(0).getUri());

                        }


                    }
                }


            }
        });
    }

    private void compressImages(List<Uri> uriList,int current,List<File> fileList,Tiny.FileCompressOptions options){
        Tiny.getInstance().source(uriList.get(current)).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                //return the compressed file path
                if(GenelUtil.isAlive(UploadActivity.this)){
                    if(!isSuccess){
                        Log.e("TAG", "callback: ", t);
                        toast(getString(R.string.error));
                        progressDialog.dismiss();
                    }
                    else{
                        fileList.add(new File(outfile));
                        if(current==uriList.size()-1){
                            //Sıkıştırma bitmiş. Yüklemeye geç
                            uploadImages(fileList,0,new ArrayList<ParseFile>());
                        }
                        else{
                            compressImages(uriList,current+1,fileList,options);
                        }
                    }
                }
            }
        });

    }

    private void uploadImages(List<File> fileList, int current,List<ParseFile> parseFileList){
        File file = fileList.get(current);
        ParseFile media = new ParseFile(file);
        media.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(GenelUtil.isAlive(UploadActivity.this)){
                    if(e==null){
                        parseFileList.add(media);
                        if(fileList.size()==current+1){
                            //Yükleme bitti. Fonksiyonu çağır
                            callFunction(parseFileList);
                        }
                        else{
                            //yükleme devam ediyor
                            uploadImages(fileList,current+1,parseFileList);
                        }
                    }
                    else{
                        toast(getString(R.string.error));
                        progressDialog.dismiss();
                    }
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer percentDone) {
                if(GenelUtil.isAlive(UploadActivity.this)){

                    float maxYuzdeDosyaBasi = 100f/(float)fileList.size();

                    float currentYuzde = percentDone/(float)fileList.size();

                    float finalYuzde = ((current+1)*maxYuzdeDosyaBasi)+currentYuzde;
                    if(finalYuzde>100){
                        finalYuzde = 100;
                    }

                    progressDialog.setMessage(getString(R.string.uploading)+" ("+Math.round(finalYuzde)+"%)");
                }
            }
        });
    }

    private void callFunction(List<ParseFile> parseFileList){
        progressDialog.setMessage(getString(R.string.finishingup));
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("text", postdesc.getText().toString().trim());
        params.put("medialist",parseFileList);
        if(parseFileList.size()>0){
            params.put("media",parseFileList.get(0));
        }
        if(parseFileList.size()>1){
            params.put("media1",parseFileList.get(1));
        }
        if(parseFileList.size()>2){
            params.put("media2",parseFileList.get(2));
        }
        if(parseFileList.size()>3){
            params.put("media3",parseFileList.get(3));
        }

        ParseCloud.callFunctionInBackground("shareImage", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean postID, ParseException e) {
                if(!UploadActivity.this.isFinishing()&&!UploadActivity.this.isDestroyed()){
                    if(e==null){

                        if(postID){
                            GenelUtil.saveUploadTexts(postdesc.getText().toString().trim());
                            //GenelUtil.saveUploadUri(uri.toString());
                            Intent data = new Intent();
                            data.putExtra("post","postID");
                            data.setData(Uri.parse("postID.getObjectId()"));
                            setResult(RESULT_OK, data);
                            progressDialog.dismiss();
                            finish();
                        }
                        else{
                            progressDialog.dismiss();
                            GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        Log.e("error",e.getLocalizedMessage());
                        GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                    }
                }

            }
        });
    }



    private void loadVideoNormally(Uri uri){
        //FFmpeg.getInstance(this).isSupported()

            progressDialog.setMessage(getString(R.string.loading));
            Observable.fromCallable(() -> {

                try {
                    GenelUtil.copy(uri,new File(UploadActivity.this.getCacheDir()+File.separator+"copiedvideo.mp4"),UploadActivity.this,progressDialog);
                    return true;
                } catch (IOException e) {

                    e.printStackTrace();
                    return false;

                }

            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {
                        //Use result for something
                        if(!result){
                            if(GenelUtil.isAlive(UploadActivity.this)){
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getString(R.string.unsupportedvideo)+"While copy",getApplicationContext());
                            }
                        }
                        else{


                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(UploadActivity.this.getCacheDir()+File.separator+"copiedvideo.mp4");
                            int videoW = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                            long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                            long fileSize = new File(UploadActivity.this.getCacheDir()+File.separator+"copiedvideo.mp4").length();
                            long sizeKB = fileSize/1000;
                            if(sizeKB/(duration/1000)>=67){
                                int resultWidth = videoW;//500
                                if(videoW>640){
                                    resultWidth = 640;
                                }
                                VideoUtils.compressVideo(UploadActivity.this.getCacheDir() + File.separator + "copiedvideo.mp4"
                                        , String.valueOf(resultWidth)
                                        ,duration
                                        , UploadActivity.this
                                        , new VideoCompressListener() {
                                            @Override
                                            public void onSuccess(File file) {
                                                if(GenelUtil.isAlive(UploadActivity.this)){
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.setMessage(getString(R.string.uploading)+" ("+0+"%)");

                                                            ParseFile video = new ParseFile(file);
                                                            video.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if(e==null){

                                                                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                                                        retriever.setDataSource(UploadActivity.this,list.get(0).getUri());
                                                                        Bitmap bitmap = retriever.getFrameAtTime(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/2);
                                                                        ParseFile thumbnail = new ParseFile(
                                                                                GenelUtil.getBytesFromBitmap(bitmap));
                                                                        thumbnail.saveInBackground(new SaveCallback() {
                                                                                                       @Override
                                                                                                       public void done(ParseException e) {
                                                                                                           if(GenelUtil.isAlive(UploadActivity.this)){
                                                                                                               if(e==null){
                                                                                                                   progressDialog.setMessage(getString(R.string.finishingup));
                                                                                                                   HashMap<String, Object> params = new HashMap<>();
                                                                                                                   params.put("text", postdesc.getText().toString().trim());
                                                                                                                   params.put("mainmedia",video);
                                                                                                                   params.put("thumbnail",thumbnail);

                                                                                                                   ParseCloud.callFunctionInBackground("shareVideo", params, new FunctionCallback<Boolean>() {
                                                                                                                       @Override
                                                                                                                       public void done(Boolean postID, ParseException e) {
                                                                                                                           if(!UploadActivity.this.isFinishing()&&!UploadActivity.this.isDestroyed()){
                                                                                                                               if(e==null){

                                                                                                                                   if(postID){
                                                                                                                                       GenelUtil.saveUploadTexts(postdesc.getText().toString().trim());
                                                                                                                                       GenelUtil.saveUploadUri(uri.toString());
                                                                                                                                       Intent data = new Intent();
                                                                                                                                       data.putExtra("post","postID");
                                                                                                                                       data.setData(Uri.parse("postID.getObjectId()"));
                                                                                                                                       setResult(RESULT_OK, data);
                                                                                                                                       progressDialog.dismiss();
                                                                                                                                       finish();
                                                                                                                                   }
                                                                                                                                   else{
                                                                                                                                       progressDialog.dismiss();
                                                                                                                                       GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                                                                                                                                   }


                                                                                                                               }
                                                                                                                               else{
                                                                                                                                   progressDialog.dismiss();
                                                                                                                                   GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                                                                                                                               }
                                                                                                                           }

                                                                                                                       }
                                                                                                                   });
                                                                                                               }
                                                                                                               else{
                                                                                                                   toast(getString(R.string.error));
                                                                                                                   progressDialog.dismiss();
                                                                                                                   Log.e("error code",""+e.getCode());
                                                                                                                   Log.e("error message",""+e.getLocalizedMessage());
                                                                                                               }
                                                                                                           }

                                                                                                       }
                                                                                                   }
                                                                                ,new ProgressCallback() {
                                                                                    @Override
                                                                                    public void done(Integer percentDone) {
                                                                                        if(GenelUtil.isAlive(UploadActivity.this)){
                                                                                            if((90+Math.round(percentDone/10))<101){
                                                                                                progressDialog.setMessage(getString(R.string.uploading)+" ("+(90+Math.round(percentDone/10))+"%)");
                                                                                            }
                                                                                        }


                                                                                    }
                                                                                });

                                                                    }
                                                                    else{
                                                                        toast(getString(R.string.error));
                                                                        progressDialog.dismiss();
                                                                        Log.e("error code",""+e.getCode());
                                                                        Log.e("error message",""+e.getLocalizedMessage());
                                                                    }
                                                                }
                                                            }, new ProgressCallback() {
                                                                @Override
                                                                public void done(Integer percentDone) {
                                                                    if(GenelUtil.isAlive(UploadActivity.this)){
                                                                        progressDialog.setMessage(getString(R.string.uploading)+" ("+Math.round((percentDone*9)/10)+"%)");
                                                                    }


                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            }

                                            @Override
                                            public void OnError(String message) {
                                                if(GenelUtil.isAlive(UploadActivity.this)){
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            toast(getString(R.string.unsupportedvideo));
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onProgress(int progress) {
                                                if(GenelUtil.isAlive(UploadActivity.this)){
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.setMessage(getString(R.string.preparingvideo)+"("+Math.round(progress)+"%)");
                                                        }
                                                    });
                                                }

                                            }
                                        });
                            }
                            else {
                                progressDialog.setMessage(getString(R.string.uploading)+" ("+0+"%)");

                                ParseFile video = new ParseFile(new File(UploadActivity.this.getCacheDir()+File.separator+"copiedvideo.mp4"));
                                video.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){

                                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                            retriever.setDataSource(UploadActivity.this,list.get(0).getUri());
                                            Bitmap bitmap = retriever.getFrameAtTime(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/2);
                                            ParseFile thumbnail = new ParseFile(
                                                    GenelUtil.getBytesFromBitmap(bitmap));
                                            thumbnail.saveInBackground(new SaveCallback() {
                                                                           @Override
                                                                           public void done(ParseException e) {
                                                                               if(GenelUtil.isAlive(UploadActivity.this)){
                                                                                   if(e==null){
                                                                                       progressDialog.setMessage(getString(R.string.finishingup));
                                                                                       HashMap<String, Object> params = new HashMap<>();
                                                                                       params.put("text", postdesc.getText().toString().trim());
                                                                                       params.put("mainmedia",video);
                                                                                       params.put("thumbnail",thumbnail);

                                                                                       ParseCloud.callFunctionInBackground("shareVideo", params, new FunctionCallback<Boolean>() {
                                                                                           @Override
                                                                                           public void done(Boolean postID, ParseException e) {
                                                                                               if(!UploadActivity.this.isFinishing()&&!UploadActivity.this.isDestroyed()){
                                                                                                   if(e==null){

                                                                                                       if(postID){
                                                                                                           GenelUtil.saveUploadTexts(postdesc.getText().toString().trim());
                                                                                                           GenelUtil.saveUploadUri(uri.toString());
                                                                                                           Intent data = new Intent();
                                                                                                           data.putExtra("post","postID");
                                                                                                           data.setData(Uri.parse("postID.getObjectId()"));
                                                                                                           setResult(RESULT_OK, data);
                                                                                                           progressDialog.dismiss();
                                                                                                           finish();
                                                                                                       }
                                                                                                       else{
                                                                                                           progressDialog.dismiss();
                                                                                                           GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                                                                                                       }


                                                                                                   }
                                                                                                   else{
                                                                                                       progressDialog.dismiss();
                                                                                                       GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                                                                                                   }
                                                                                               }

                                                                                           }
                                                                                       });
                                                                                   }
                                                                                   else{
                                                                                       toast(getString(R.string.error));
                                                                                       progressDialog.dismiss();
                                                                                       Log.e("error code",""+e.getCode());
                                                                                       Log.e("error message",""+e.getLocalizedMessage());
                                                                                   }
                                                                               }

                                                                           }
                                                                       }
                                                    ,new ProgressCallback() {
                                                        @Override
                                                        public void done(Integer percentDone) {
                                                            if(GenelUtil.isAlive(UploadActivity.this)){
                                                                if((90+Math.round(percentDone/10))<101){
                                                                    progressDialog.setMessage(getString(R.string.uploading)+" ("+(90+Math.round(percentDone/10))+"%)");
                                                                }
                                                            }


                                                        }
                                                    });

                                        }
                                        else{
                                            toast(getString(R.string.error));
                                            progressDialog.dismiss();
                                            Log.e("error code",""+e.getCode());
                                            Log.e("error message",""+e.getLocalizedMessage());
                                        }
                                    }
                                }, new ProgressCallback() {
                                    @Override
                                    public void done(Integer percentDone) {
                                        if(GenelUtil.isAlive(UploadActivity.this)){
                                            progressDialog.setMessage(getString(R.string.uploading)+" ("+Math.round((percentDone*9)/10)+"%)");
                                        }


                                    }
                                });
                            }





                        }

                    });






    }












    @Override
    public void onBackPressed() {
        if(uploadbutton!=null){
            if(uploadbutton.getVisibility()==View.VISIBLE){
                new AlertDialog.Builder(UploadActivity.this)
                        .setTitle(getString(R.string.exit))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                finish();
                            }

                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();

            }
            else{
                finish();
            }
        }
        else{
            finish();
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(GenelUtil.isAlive(this)){


            if(resultCode== RESULT_OK&&data!=null){

                if(requestCode==imagerequestcode){
                    List<Uri> tList = Matisse.obtainResult(data);
                    if(adapter==null){

                        list = new ArrayList<>();
                        for(int i = 0;i<tList.size();i++){
                            list.add(new UploadObject(tList.get(i),"image"));
                        }
                        if(list.size()<4){
                            list.add(new UploadObject(null,"add"));
                        }



                        adapter = new UploadPostAdapter();
                        adapter.setContext(list,Glide.with(UploadActivity.this),UploadActivity.this);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    else{
                        if(list.size()>0){
                            if(list.get(list.size()-1).getType().equals("add")){
                                list.remove(list.size()-1);
                            }
                        }

                        for(int i = 0;i<tList.size();i++){
                            list.add(new UploadObject(tList.get(i),"image"));
                        }
                        if(list.size()<4){
                            list.add(new UploadObject(null,"add"));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    uploadsec.setVisibility(View.GONE);

                    uploadbutton.setVisibility(View.VISIBLE);

                }

                else if(requestCode==videorequestcode){
                    progressDialog.show();
                    List<Uri> tList = Matisse.obtainResult(data);
                    if(tList.size()>0){
                        Uri uri = tList.get(0);
                        try{
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(this,uri);

                            long sure = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                            Log.e("Video Suresi :",sure+"");
                            if(sure>60500){
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getApplicationContext(),getString(R.string.videosure));
                            }
                            else{
                                if(adapter==null){
                                    list = new ArrayList<>();
                                    list.add(new UploadObject(uri,"video"));

                                    adapter = new UploadPostAdapter();
                                    adapter.setContext(list,Glide.with(UploadActivity.this),UploadActivity.this);
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                                    linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                                else{
                                    if(list.size()>0){
                                        if(list.get(list.size()-1).getType().equals("add")){
                                            list.remove(list.size()-1);
                                        }
                                    }
                                    if(list.size()==0){
                                        list.add(new UploadObject(uri,"video"));
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                                uploadsec.setVisibility(View.GONE);
                                uploadbutton.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();

                            }
                        }catch (Exception e){
                            GenelUtil.ToastLong(UploadActivity.this,getString(R.string.error)+e.getMessage());
                            progressDialog.dismiss();
                        }
                    }


                }

                else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri uri=result.getUri();
                    list.get(editPosition).setUri(uri);
                    adapter.notifyDataSetChanged();
                }


            }
        }

    }

    private void toast(String string) {
        Toast.makeText(getApplicationContext(), string,Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onCancel(int position) {
        list.remove(position);
        if(list.size()==1){
            if(list.get(0).getType().equals("add")){
                list.remove(0);
            }
        }

        if(list.size()==0){
            uploadsec.setVisibility(View.VISIBLE);
        }
        if(list.size()<4){
            if(list.size()>0){
                if(!list.get(list.size()-1).getType().equals("add")){
                    list.add(new UploadObject(null,"add"));
                }
            }

        }
        adapter.notifyDataSetChanged();
    }

    int editPosition = 0;

    @Override
    public void onEdit(int position) {
        editPosition=position;

        try {
            CropImage.activity(list.get(position).getUri())
                    .setActivityTitle(getString(R.string.cropimage))
                    .setCropMenuCropButtonTitle(getString(R.string.crop)).start(this);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAdd() {

        selectImage();
        //addphoto.performClick();

    }

    private void selectImage(){
        int a = 4;
        if(list!=null){
            if(list.size()>0){
                if(list.get(list.size()-1).getType().equals("add")){
                    a = 5-list.size();
                }
                else{
                    a = 4-list.size();
                }
            }

        }


        Matisse.from(UploadActivity.this)
                .choose(MimeType.of(MimeType.JPEG,MimeType.PNG))
                .countable(true)
                .maxSelectable(a)
                //.gridExpectedSize(3)
                .showSingleMediaType(true)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(true) // Default is `true`
                .forResult(imagerequestcode);


    }

    private void selectVideo(){
        Matisse.from(UploadActivity.this)
                .choose(MimeType.of(MimeType.MP4,MimeType.AVI,MimeType.MKV,MimeType.MPEG))
                .countable(true)
                .maxSelectable(1)
                //.gridExpectedSize(3)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .showSingleMediaType(true)
                .imageEngine(new GlideEngine())
                .showPreview(true) // Default is `true`
                .forResult(videorequestcode);
    }
}
