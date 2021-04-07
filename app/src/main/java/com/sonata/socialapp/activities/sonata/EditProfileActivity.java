package com.sonata.socialapp.activities.sonata;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


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
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.FileCompressListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static com.sonata.socialapp.utils.GenelUtil.getBase64;

public class EditProfileActivity extends AppCompatActivity {
    SonataUser user;
    RelativeLayout ppclick;
    ImageView imageView;
    EditText name,bio;
    RelativeLayout back,save;
    private int imagerequestcode=9514;
    boolean ppremove = false;
    ProgressDialog progressDialog;
    Uri uri;

    private long mLastClickTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        ppclick = findViewById(R.id.editprofileppclick);
        imageView = findViewById(R.id.profilephotophoto);
        name = findViewById(R.id.nameedittext);
        bio = findViewById(R.id.bioedittext);
        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        user = (SonataUser) ParseUser.getCurrentUser();
        if(user.getHasPp()){
            Glide.with(this).load(user.getPPbig()).into(imageView);
        }
        else{
            Glide.with(this).load(getResources().getDrawable(R.drawable.emptypp,null)).into(imageView);
        }


        name.setText(user.getName());
        bio.setText(user.getBio());
        ppclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> other = new ArrayList<>();
                other.add(getString(R.string.newphoto));
                if(user.getHasPp()){
                    other.add(getString(R.string.removephoto));
                }

                String[] popupMenu = other.toArray(new String[other.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setCancelable(true);
                builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String result = popupMenu[i];
                        if(result.equals(getString(R.string.newphoto))){
                            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            Permissions.check(EditProfileActivity.this,permissions,null, null, new PermissionHandler() {
                                @Override
                                public void onGranted() {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(Intent.createChooser(intent,getString(R.string.photo)),imagerequestcode);
                                }
                                @Override
                                public void onDenied(Context context, ArrayList<String> deniedPermissions){

                                }
                            });
                        }
                        else{
                            uri=null;
                            ppremove=true;
                            Glide.with(EditProfileActivity.this).load(getResources().getDrawable(R.drawable.emptypp,null)).into(imageView);


                        }
                    }
                }).show();
            }
        });


        save = findViewById(R.id.uploadtextripple);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                progressDialog.show();
                progressDialog.setMessage(getString(R.string.uploading));
                if(uri!=null){
                    progressDialog.setMessage(getString(R.string.preparingimage));
                    prepareImage();
                }
                else{

                    HashMap<String, Object> params = new HashMap<String, Object>();

                    params.put("ppremove", ppremove);
                    params.put("name", name.getText().toString().trim());
                    params.put("bio", bio.getText().toString().trim());
                    ParseCloud.callFunctionInBackground("updateOwnProfile", params, new FunctionCallback<HashMap>() {
                        @Override
                        public void done(HashMap object, ParseException e) {

                            if(e==null){
                                progressDialog.dismiss();
                                finish();
                            }
                            else{

                                progressDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });

    }

    private void prepareImage(){

        GenelUtil.compressImage(this,uri, 55, new FileCompressListener() {
            @Override
            public void done(File file) {

                ParseFile media = new ParseFile(file);
                media.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            if(GenelUtil.isAlive(EditProfileActivity.this)){
                                progressDialog.setMessage(getString(R.string.finishingup));
                                HashMap<String, Object> params = new HashMap<String, Object>();
                                params.put("pp", media);
                                params.put("name", name.getText().toString().trim());
                                params.put("bio", bio.getText().toString().trim());
                                ParseCloud.callFunctionInBackground("updateOwnProfile", params, new FunctionCallback<HashMap>() {
                                    @Override
                                    public void done(HashMap object, ParseException e) {
                                        if(e==null){
                                            GenelUtil.saveNewUser(GenelUtil.convertUserToJson((SonataUser) object.get("user")),EditProfileActivity.this);
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                        else{
                                            Log.e("error",e.getMessage());
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            progressDialog.dismiss();
                            GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        if(GenelUtil.isAlive(EditProfileActivity.this)){
                            progressDialog.setMessage(getString(R.string.uploading)+" ("+percentDone+"%)");
                        }
                    }
                });
            }

            @Override
            public void error(String message) {
                progressDialog.dismiss();
                GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==imagerequestcode&&resultCode==RESULT_OK&&data.getData()!=null){

            try{
                CropImage.activity(data.getData())
                        .setActivityTitle(getString(R.string.resmikirp))
                        .setAllowFlipping(false)
                        .setAspectRatio(1,1)
                        .setCropMenuCropButtonTitle(getString(R.string.kirp))
                        .start(this);

            }catch (Exception e){
                GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
            }

        }
        else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            uri=result.getUri();
            Glide.with(this).load(uri).into(imageView);
        }


    }
}
