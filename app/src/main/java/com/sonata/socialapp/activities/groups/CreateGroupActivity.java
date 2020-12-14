package com.sonata.socialapp.activities.groups;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.EditProfileActivity;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.Group;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.util.HashMap;

import static com.sonata.socialapp.utils.GenelUtil.getBase64;

public class CreateGroupActivity extends AppCompatActivity {

    RelativeLayout back, selectBanner, create;
    private int imagerequestcode=9514;
    Uri uri;
    EditText name, desc;
    ImageView imageView;
    ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        progressDialog = new ProgressDialog(this);


        progressDialog.setMessage(getString(R.string.creatinggroup));
        name = findViewById(R.id.nameedittext);
        desc = findViewById(R.id.bioedittext);
        imageView = findViewById(R.id.createnewgrouptopbanner);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectBanner = findViewById(R.id.select_new_image);
        selectBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,getString(R.string.photo)),imagerequestcode);
            }
        });

        create = findViewById(R.id.creategroupripple);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri!=null&&name.getText().toString().trim().length()>0&&desc.getText().toString().trim().length()>0){
                    prepareImage(uri,0);
                }
                else{
                    startActivity(new Intent(CreateGroupActivity.this,GroupActivity.class));
                }
            }
        });

    }


    private void prepareImage(Uri uri,int tr){
        progressDialog.show();
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.size=55;
        Tiny.getInstance().source(uri.getPath()).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                if(GenelUtil.isAlive(CreateGroupActivity.this)){
                    if(!isSuccess){
                        if(tr<5){
                            prepareImage(uri,tr+1);
                        }
                        else{
                            progressDialog.dismiss();
                            GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                        }

                    }
                    else{
                        uploadImage(new File(outfile),0);
                    }
                }
            }
        });
    }

    private void uploadImage(File file ,int tr){
        ParseFile media = new ParseFile(file);
        media.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    if(GenelUtil.isAlive(CreateGroupActivity.this)){
                        callFunction(media);
                    }
                }
                else{
                    if(tr<3){
                        uploadImage(file,tr+1);
                    }
                    else{
                        progressDialog.dismiss();
                        GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                    }

                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer percentDone) {
                if(GenelUtil.isAlive(CreateGroupActivity.this)){
                    progressDialog.setMessage(getString(R.string.uploading)+" ("+percentDone+"%)");
                }
            }
        });
    }

    private void callFunction(ParseFile media){
        progressDialog.setMessage(getString(R.string.finishingup));
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("image", media);
        params.put("name", name.getText().toString().trim());
        params.put("desc", desc.getText().toString().trim());
        ParseCloud.callFunctionInBackground("createNewGroup", params, new FunctionCallback<Group>() {
            @Override
            public void done(Group object, ParseException e) {
                if(e==null){
                    progressDialog.dismiss();
                    startActivity(new Intent(CreateGroupActivity.this,GroupActivity.class).putExtra("group",object));
                    finish();
                }
                else{
                    Log.e("error",e.getMessage());
                    progressDialog.dismiss();
                }
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
                        .setAspectRatio(10,5)
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
