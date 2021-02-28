package com.sonata.socialapp.activities.groups;

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
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.socialview.SocialAutoCompleteTextView;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.UploadPostAdapter;
import com.sonata.socialapp.utils.classes.Group;
import com.sonata.socialapp.utils.classes.UploadObject;
import com.sonata.socialapp.utils.interfaces.UploadPostClick;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupUploadActivity extends AppCompatActivity implements UploadPostClick {

    Group group;

    ImageView groupimage;
    TextView groupname,groupdesc;

    Uri uri;
    List<UploadObject> list;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    UploadPostAdapter adapter;
    private boolean loading=true;
    private long mLastClickTime = 0;
    private final int imagerequestcode = 9514;
    private final int videorequestcode = 8403;

    RelativeLayout uploadsec;
    SocialAutoCompleteTextView postdesc;
    RelativeLayout addphoto,addvideo,backbutton,uploadbutton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_upload);

        group = getIntent().getParcelableExtra("group");
        if(group==null){
            return;
        }

        groupimage = findViewById(R.id.group_image);
        groupname = findViewById(R.id.groupname);
        groupdesc = findViewById(R.id.groupdesc);

        Glide.with(this).load(group.getImageUrl()).into(groupimage);
        groupname.setText(group.getName());
        groupdesc.setText(group.getDescription());


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

        postdesc.setHashtagColor(getResources().getColor(R.color.blue));
        postdesc.setMentionColor(getResources().getColor(R.color.blue));
        postdesc.setHyperlinkColor(getResources().getColor(R.color.blue));

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
                Permissions.check(GroupUploadActivity.this,permissions,null, null, new PermissionHandler() {
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
                Permissions.check(GroupUploadActivity.this,permissions,null, null, new PermissionHandler() {
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
                GenelUtil.hideKeyboard(GroupUploadActivity.this);
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
                if(GenelUtil.isAlive(GroupUploadActivity.this)){
                    if(!isSuccess){
                        toast(getString(R.string.error)+t.getMessage());
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

                if(GenelUtil.isAlive(GroupUploadActivity.this)){
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
                if(GenelUtil.isAlive(GroupUploadActivity.this)){

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
        params.put("groupid",group.getId());

        ParseCloud.callFunctionInBackground("shareImageToGroup", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean postID, ParseException e) {
                if(!GroupUploadActivity.this.isFinishing()&&!GroupUploadActivity.this.isDestroyed()){
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
                GenelUtil.copy(uri,new File(GroupUploadActivity.this.getCacheDir()+File.separator+"copiedvideo.mp4"),GroupUploadActivity.this,progressDialog);
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
                        if(GenelUtil.isAlive(GroupUploadActivity.this)){
                            progressDialog.dismiss();
                            GenelUtil.ToastLong(getString(R.string.unsupportedvideo)+"While copy",getApplicationContext());
                        }
                    }
                    else{
                        progressDialog.setMessage(getString(R.string.preparingvideo));



                        /*VideoCompress.compressVideoMedium(GroupUploadActivity.this.getCacheDir() + File.separator + "copiedvideo.mp4", GroupUploadActivity.this.getCacheDir() + File.separator + "video.mp4", new VideoCompress.CompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess() {
                                if(GenelUtil.isAlive(GroupUploadActivity.this)){
                                    progressDialog.setMessage(getString(R.string.uploading)+" ("+0+"%)");

                                    ParseFile video = new ParseFile(new File(GroupUploadActivity.this.getCacheDir() + File.separator + "video.mp4"));
                                    video.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){

                                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                                retriever.setDataSource(GroupUploadActivity.this,list.get(0).getUri());
                                                Bitmap bitmap = retriever.getFrameAtTime(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/2);
                                                ParseFile thumbnail = new ParseFile(
                                                        GenelUtil.getBytesFromBitmap(bitmap));
                                                thumbnail.saveInBackground(new SaveCallback() {
                                                                               @Override
                                                                               public void done(ParseException e) {
                                                                                   if(GenelUtil.isAlive(GroupUploadActivity.this)){
                                                                                       if(e==null){
                                                                                           progressDialog.setMessage(getString(R.string.finishingup));
                                                                                           HashMap<String, Object> params = new HashMap<>();
                                                                                           params.put("text", postdesc.getText().toString().trim());
                                                                                           params.put("mainmedia",video);
                                                                                           params.put("thumbnail",thumbnail);
                                                                                           params.put("groupid",group.getId());
                                                                                           ParseCloud.callFunctionInBackground("shareVideoToGroup", params, new FunctionCallback<Boolean>() {
                                                                                               @Override
                                                                                               public void done(Boolean postID, ParseException e) {
                                                                                                   if(!GroupUploadActivity.this.isFinishing()&&!GroupUploadActivity.this.isDestroyed()){
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
                                                                if(GenelUtil.isAlive(GroupUploadActivity.this)){
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
                                            if(GenelUtil.isAlive(GroupUploadActivity.this)){
                                                progressDialog.setMessage(getString(R.string.uploading)+" ("+Math.round((percentDone*9)/10)+"%)");
                                            }


                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFail() {
                                if(GenelUtil.isAlive(GroupUploadActivity.this)){
                                    toast(getString(R.string.unsupportedvideo));
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onProgress(float percent) {
                                if(GenelUtil.isAlive(GroupUploadActivity.this)){
                                    progressDialog.setMessage(getString(R.string.preparingvideo)+"("+Math.round(percent)+"%)");
                                }
                            }
                        });*/
                    }

                });


    }












    @Override
    public void onBackPressed() {
        if(uploadbutton!=null){
            if(uploadbutton.getVisibility()==View.VISIBLE){
                new AlertDialog.Builder(GroupUploadActivity.this)
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

                if(requestCode==13271){
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
                        adapter.setContext(list, Glide.with(GroupUploadActivity.this),GroupUploadActivity.this);
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

                else if(requestCode==24381){
                    progressDialog.show();
                    List<Uri> tList = Matisse.obtainResult(data);
                    if(tList.size()>0){
                        Uri uri = tList.get(0);
                        try{
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(this,uri);

                            long sure = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                            Log.e("Video Suresi :",sure+"");
                            if(sure>60100){
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getApplicationContext(),getString(R.string.videosure));
                            }
                            else{
                                if(adapter==null){
                                    list = new ArrayList<>();
                                    list.add(new UploadObject(uri,"video"));

                                    adapter = new UploadPostAdapter();
                                    adapter.setContext(list,Glide.with(GroupUploadActivity.this),GroupUploadActivity.this);
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
                            GenelUtil.ToastLong(GroupUploadActivity.this,getString(R.string.error)+e.getMessage());
                            progressDialog.dismiss();
                        }
                    }


                }




                else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri uri=result.getUri();
                    list.get(editPosition).setUri(uri);
                    adapter.notifyDataSetChanged();

                    /*if(adapter==null){

                        list = new ArrayList<>();
                        list.add(new UploadObject(uri,"image"));
                        list.add(new UploadObject(null,"add"));

                        adapter = new UploadPostAdapter();
                        adapter.setContext(list,Glide.with(GroupUploadActivity.this),GroupUploadActivity.this);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(list.size()-1);
                    }
                    else{
                        if(list.size()>0){
                            if(list.get(list.size()-1).getType().equals("add")){
                                list.remove(list.size()-1);


                            }
                        }

                        if(list.size()<=3){
                            list.add(new UploadObject(uri,"image"));
                        }
                        if(list.size()<4){
                            list.add(new UploadObject(null,"add"));
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(list.size()-2);
                    }
                    uploadsec.setVisibility(View.GONE);

                    uploadbutton.setVisibility(View.VISIBLE);*/


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
                    a=4-list.size();
                }
            }

        }
        Matisse.from(GroupUploadActivity.this)
                .choose(MimeType.of(MimeType.JPEG,MimeType.PNG))
                .countable(true)
                .maxSelectable(a)
                //.gridExpectedSize(3)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(true) // Default is `true`
                .forResult(13271 );
    }

    private void selectVideo(){
        Matisse.from(GroupUploadActivity.this)
                .choose(MimeType.of(MimeType.MP4,MimeType.AVI,MimeType.MKV,MimeType.MPEG))
                .countable(true)
                .maxSelectable(1)
                //.gridExpectedSize(3)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(true) // Default is `true`
                .forResult(24381 );
    }
}
