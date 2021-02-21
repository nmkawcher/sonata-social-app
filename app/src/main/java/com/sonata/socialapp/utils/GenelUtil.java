package com.sonata.socialapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.listeners.OnDismissListener;
import com.tylersuehr.socialtextview.SocialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.parse.Parse.getApplicationContext;


public class GenelUtil {

    public static Comment comment = null;
    private static long a;
    public static final int ACCOUNT_LIMIT = 5;

    public static void editSavedUser(String id, Context context){

    }

    public static void updateUser(JSONObject user,Context context){
        SharedPreferences pref = context.getSharedPreferences("savedAccounts", 0);
        String userArrayStr = pref.getString("accounts","");

        try {
            if(userArrayStr != null && userArrayStr.length()>0){
                Log.e("deneme","string not null");
                JSONObject jsonObject = new JSONObject(userArrayStr);
                JSONArray array = jsonObject.getJSONArray("users");



                    JSONObject jsonObject2 = new JSONObject();
                    JSONArray newArr = new JSONArray();
                    newArr.put(user);

                    for(int i=0;i<array.length();i++){
                        JSONObject us = array.getJSONObject(i);
                        if(!us.get("id").equals(user.get("id"))){
                            newArr.put(us);
                        }
                    }

                    jsonObject2.put("users",newArr);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("accounts",jsonObject2.toString());
                    editor.apply();


            }
            else{
                Log.e("deneme","string null yada uzunluk 0");
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                array.put(user);
                jsonObject.put("users",array);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("accounts",jsonObject.toString());
                editor.apply();
            }

        } catch (JSONException e) {
            Log.e("saveUser",e.toString());
            e.printStackTrace();
        }

    }


    public static void updateUser2(JSONObject user,Context context){
        SharedPreferences pref = context.getSharedPreferences("savedAccounts", 0);
        String userArrayStr = pref.getString("accounts","");

        try {
            if(userArrayStr != null && userArrayStr.length()>0){
                Log.e("deneme","string not null");
                JSONObject jsonObject = new JSONObject(userArrayStr);
                JSONArray array = jsonObject.getJSONArray("users");



                JSONObject jsonObject2 = new JSONObject();
                JSONArray newArr = new JSONArray();
                newArr.put(user);

                for(int i=0;i<array.length();i++){
                    JSONObject us = array.getJSONObject(i);
                    if(!us.get("id").equals(user.get("id"))){
                        newArr.put(us);
                    }
                }

                jsonObject2.put("users",newArr);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("accounts",jsonObject2.toString());
                editor.apply();


            }
            else{
                Log.e("deneme","string null yada uzunluk 0");
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                array.put(user);
                jsonObject.put("users",array);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("accounts",jsonObject.toString());
                editor.apply();
            }

        } catch (JSONException e) {
            Log.e("saveUser",e.toString());
            e.printStackTrace();
        }

    }



    public static void saveNewUser(JSONObject user,Context context){
        SharedPreferences pref = context.getSharedPreferences("savedAccounts", 0);
        String userArrayStr = pref.getString("accounts","");

        try {
            if(userArrayStr != null && userArrayStr.length()>0){
                Log.e("deneme","string not null");
                JSONObject jsonObject = new JSONObject(userArrayStr);
                JSONArray array = jsonObject.getJSONArray("users");

                if(array.length() < ACCOUNT_LIMIT){

                    JSONObject jsonObject2 = new JSONObject();
                    JSONArray newArr = new JSONArray();
                    newArr.put(user);

                    for(int i=0;i<array.length();i++){
                        JSONObject us = array.getJSONObject(i);
                        if(!us.get("id").equals(user.get("id"))){
                            newArr.put(us);
                        }
                    }

                    jsonObject2.put("users",newArr);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("accounts",jsonObject2.toString());
                    editor.apply();

                }
            }
            else{
                Log.e("deneme","string null yada uzunluk 0");
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                array.put(user);
                jsonObject.put("users",array);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("accounts",jsonObject.toString());
                editor.apply();
            }

        } catch (JSONException e) {
            Log.e("saveUser",e.toString());
            e.printStackTrace();
        }

    }

    public static void removeUserFromCache(String id,Context context){

        JSONObject jsonObject = new JSONObject();
        JSONArray array = getSavedUsers(context);
        JSONArray newArr = new JSONArray();
        for(int i=0;i<array.length();i++){
            JSONObject us = null;
            try {
                us = array.getJSONObject(i);
                if(!us.get("id").equals(id)){
                    newArr.put(us);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        try {
            jsonObject.put("users",newArr);
            SharedPreferences pref = context.getSharedPreferences("savedAccounts", 0);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString("accounts",jsonObject.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getSavedUsers(Context context){
        SharedPreferences pref = context.getSharedPreferences("savedAccounts", 0);
        String userArrayStr = pref.getString("accounts","");
        try {
            assert userArrayStr != null;
            return new JSONObject(userArrayStr).getJSONArray("users");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }



    public static List<Object> isUserSaved(Context context, String id){
        List<Object> list = new ArrayList<>();
        boolean exist = false;
        String username = "";
        String session = "";
        JSONArray array = getSavedUsers(context);
        for(int i=0;i<array.length();i++){
            try {
                JSONObject user = (JSONObject) array.get(i);
                if(user.getString("id").equals(id)){
                    exist = true;
                    username = user.getString("username");
                    session = user.getString("session");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        list.add(exist);
        list.add(username);
        list.add(session);
        return list;
    }

    public static JSONArray getSavedUsersFinal(Context context){
        JSONArray ar2 = new JSONArray();
        JSONArray array = getSavedUsers(context);
        if(ParseUser.getCurrentUser()!=null){
            ar2.put(convertUserToJson((SonataUser) ParseUser.getCurrentUser()));
            for(int i=0;i<array.length();i++){
                JSONObject us = null;
                try {
                    us = array.getJSONObject(i);
                    if(!us.get("id").equals(ParseUser.getCurrentUser().getObjectId())){
                        ar2.put(us);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        else{
            for(int i=0;i<array.length();i++){
                JSONObject us = null;
                try {
                    us = array.getJSONObject(i);
                    ar2.put(us);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        return ar2;
    }

    public static JSONObject convertUserToJson(SonataUser user){
        JSONObject json = new JSONObject();
        try {
            json.put("namesurname",user.getName());
            json.put("haspp",user.getHasPp());
            json.put("id",user.getObjectId());
            json.put("username",user.getUsername());
            json.put("pp",user.getPPbig());
            json.put("session",user.getSessionToken());
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static boolean isAlive(Activity activity){
        return !activity.isFinishing() && !activity.isDestroyed();
    }

    public static Drawable getVideoDrawable(RecyclerView recyclerView,int i,int id){
        View child = recyclerView.getChildAt(i);
        if(child!=null){
            ImageView view = (ImageView) child.findViewById(id);
            return view!=null?view.getDrawable():null;
        }
        else{
            return null;
        }
    }


    public static byte[] getBytesFromDrawable(Drawable drawable){

        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }catch (Exception e){
            return new byte[0];
        }

    }



    public static byte[] getBytesFromBitmap(Bitmap bm){

        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }catch (Exception e){
            return new byte[0];
        }

    }

    public static String getBase64FromDrawable(Drawable drawable){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
    }


    public static long getProgress(String message,int toplamsure) {
        Pattern pattern = Pattern.compile("time=([\\d\\w:]+)");
        if (message.contains("speed")&&message.contains("time=")) {
            Matcher matcher = pattern.matcher(message);
            matcher.find();
            String tempTime="";
            try{
                tempTime = String.valueOf(matcher.group(1));
            }catch (Exception e){return 0;}
            String[] arrayTime = new String[0];
            try{
                arrayTime = tempTime.split(":");
            }catch (Exception e){return 0;}

            long currentTime =
                    TimeUnit.HOURS.toSeconds(Long.parseLong(arrayTime[0]))
                            + TimeUnit.MINUTES.toSeconds(Long.parseLong(arrayTime[1]))
                            + Long.parseLong(arrayTime[2]);
            if(toplamsure==0){
                return 0;
            }
            long percent = 100 * currentTime/toplamsure;

            Log.d("TAG", "currentTime -> " + currentTime + "s % -> " + percent);

            return percent;
        }
        return 0;
    }



    public static boolean clickable(long sure){
        if (SystemClock.elapsedRealtime() - a < sure){
            return false;
        }
        else{
            a = SystemClock.elapsedRealtime();
            return true;
        }
    }

    public static void saveRestore(boolean nightMode){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("restore", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("restore",nightMode);
        editor.apply();
    }

    public static boolean getRestore(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("restore", 0);
        return pref.getBoolean("restore",false);
    }

    public static void saveUploadTexts(String text){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("uploadrestore", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("utext",text);
        editor.apply();
    }

    public static String getUploadText(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("uploadrestore", 0);
        return pref.getString("utext","");
    }

    public static void saveUploadUri(String text){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("uploadrestore", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uuri",text);
        editor.apply();
    }

    public static Uri getUploadUri(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("uploadrestore", 0);
        return Uri.parse(pref.getString("uuri",""));
    }




    public static String getPathFromUri(final Uri uri,final Context context) {

        final boolean isKitKat = true;

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static void copy(Uri uri, File dst, Context context, ProgressDialog progressDialog) throws IOException {
        InputStream in = context.getContentResolver().openInputStream(uri);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }catch (Exception e){
                progressDialog.dismiss();
                ToastLong(context.getString(R.string.unsupportedvideo),context);
            }
            finally {
                out.close();
            }
        }catch (Exception e){
            progressDialog.dismiss();
            ToastLong(context.getString(R.string.unsupportedvideo),context);
        }
        finally {
            assert in != null;
            in.close();
        }
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }





    public static SonataUser getCurrentUser(){
        if(ParseUser.getCurrentUser()==null){
            return null;
        }
        return((SonataUser) ParseUser.getCurrentUser());
    }


    public static void saveNightMode(boolean nightMode){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("nightMode", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean("nightMode",nightMode);
        editor.apply();
    }

    public static boolean getNightMode(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("nightMode", 0);
        int currentNightMode = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return pref.getBoolean("nightMode",false);
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return pref.getBoolean("nightMode",true);
            default:
                return pref.getBoolean("nightMode",false);
        }
    }

    public static boolean getNightModeApp(Context context){
        SharedPreferences pref = context.getSharedPreferences("nightMode", 0);
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return pref.getBoolean("nightMode",false);
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return pref.getBoolean("nightMode",true);
            default:
                return pref.getBoolean("nightMode",false);
        }

    }









    public static String getBase64(File file){
        String base64 = "";
        try {/*from   w w w .  ja  va  2s  .  c om*/
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public static void ToastLong (Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }
    public static void ToastLong (Context context, int id){
        Toast.makeText(context,context.getString(id), Toast.LENGTH_LONG).show();
    }
    public static void ToastLong (String message,Context context){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }

    public static void ToastShort (Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    public static void showKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    public static void showImage(List<String> images, List<HashMap> items, ImageView imageView, int pos, RecyclerView.Adapter adapter){

        /*CircularProgressDrawable progressDrawable = new CircularProgressDrawable(imageView.getContext());
        progressDrawable.setCenterRadius(30f);
        progressDrawable.setStrokeWidth(3f);
        progressDrawable.setColorSchemeColors(imageView.getResources().getColor(R.color.colorRed));
        progressDrawable.start();*/


        StfalconImageViewer.Builder<String> view = new StfalconImageViewer.Builder<String>(imageView.getContext(), images, new com.stfalcon.imageviewer.loader.ImageLoader<String>() {
            @Override
            public void loadImage(ImageView imageView, String image) {
                int i = Integer.parseInt(image);
                HashMap media = items.get(i);


                int width = (int) media.get("width");
                int height = (int) media.get("height");
                ParseFile mainMedia = (ParseFile) media.get("media");
                ParseFile thumbnail = (ParseFile) media.get("thumbnail");
                Log.e("width",width+"");
                Log.e("height",height+"");
                Log.e("mainMedia",mainMedia+"");
                Log.e("thumbnail",thumbnail+"");
                if(height>1280||width>1280){
                    int ih = 1280;
                    int iw = 1280;
                    if(height>width){
                        //ih = 1280;
                        iw = 1280 * (int) (width/height);
                    }
                    else{
                        //iw = 1280;
                        ih = 1280 * (int) (height/width);
                    }
                    Glide.with(imageView)
                            .load(mainMedia.getUrl())
                            .fitCenter()
                            .override(iw, ih)
                            .thumbnail(Glide.with(imageView).load(thumbnail.getUrl()))
                            .into(imageView);
                }
                else{
                    //holder.nsfwIcon.setVisibility(View.INVISIBLE);
                    Glide.with(imageView)
                            .load(mainMedia.getUrl())
                            .fitCenter()
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .thumbnail(Glide.with(imageView).load(thumbnail.getUrl()))
                            .into(imageView);
                }


            }
        }).withHiddenStatusBar(false)
                .withBackgroundColor(Color.parseColor("#000000"));


        view.withStartPosition(pos);
        view.withDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }
            }
        });

        view.show(true);








    }

    public static void copyText(String text,Context context){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.app_name), text);
        try{
            clipboard.setPrimaryClip(clip);
        }catch (Exception e){
            GenelUtil.ToastLong(getApplicationContext(),getApplicationContext().getString(R.string.error));
        }

    }

    public static String ConvertNumber(int number, Context context){
        if(number<0){
            int newNum = number*-1;
            String sayi = String.valueOf(newNum);

            if(sayi.length()<=4){
                return "-"+sayi;
            }
            if(sayi.length()==5){
                String s5 = "-"+String.valueOf(Integer.parseInt(Long.toString(newNum).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(newNum).substring(1, 2)))+ "."+ String.valueOf(Integer.parseInt(Long.toString(newNum).substring(2, 3))) + ((Context) context).getString(R.string.bin);
                return s5;

            }
            if(sayi.length()==6){
                String s6 = "-"+String.valueOf(Integer.parseInt(Long.toString(newNum).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(newNum).substring(1, 2)))+ String.valueOf(Integer.parseInt(Long.toString(newNum).substring(2, 3)))+ ((Context) context).getString(R.string.bin);
                return s6;

            }
            if(sayi.length()==7) {
                String s7 = "-"+String.valueOf(Integer.parseInt(Long.toString(newNum).substring(0, 1))) + "." + String.valueOf(Integer.parseInt(Long.toString(newNum).substring(1, 2))) + context.getString(R.string.milyon);
                return s7;
            }
            if(sayi.length()==8){
                String s8 = "-"+String.valueOf(Integer.parseInt(Long.toString(newNum).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(newNum).substring(1, 2)))+ ((Context) context).getString(R.string.milyon);
                return s8;

            }
            if(sayi.length()==9){
                String s9 = "-"+String.valueOf(Integer.parseInt(Long.toString(newNum).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(newNum).substring(1, 2)))+ String.valueOf(Integer.parseInt(Long.toString(newNum).substring(2, 3)))+ ((Context) context).getString(R.string.milyon);
                return s9;

            }
            else{
                return "";
            }
        }
        else{
            String sayi = String.valueOf(number);

            Context activity = (Context) context;
            if(sayi.length()<=4){
                return sayi;
            }
            if(sayi.length()==5){
                String s5 = String.valueOf(Integer.parseInt(Long.toString(number).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(number).substring(1, 2)))+ "."+ String.valueOf(Integer.parseInt(Long.toString(number).substring(2, 3))) + activity.getString(R.string.bin);
                return s5;

            }
            if(sayi.length()==6){
                String s6 = String.valueOf(Integer.parseInt(Long.toString(number).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(number).substring(1, 2)))+ String.valueOf(Integer.parseInt(Long.toString(number).substring(2, 3)))+ activity.getString(R.string.bin);
                return s6;

            }
            if(sayi.length()==7) {
                String s7 = String.valueOf(Integer.parseInt(Long.toString(number).substring(0, 1))) + "." + String.valueOf(Integer.parseInt(Long.toString(number).substring(1, 2))) + activity.getString(R.string.milyon);
                return s7;
            }
            if(sayi.length()==8){
                String s8 = String.valueOf(Integer.parseInt(Long.toString(number).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(number).substring(1, 2)))+ activity.getString(R.string.milyon);
                return s8;

            }
            if(sayi.length()==9){
                String s9 = String.valueOf(Integer.parseInt(Long.toString(number).substring(0, 1)))+ String.valueOf(Integer.parseInt(Long.toString(number).substring(1, 2)))+ String.valueOf(Integer.parseInt(Long.toString(number).substring(2, 3)))+ activity.getString(R.string.milyon);
                return s9;

            }
            else{
                return "";
            }
        }

    }


    /*public static String getRealPathFromURI (Uri contentUri,Activity activity) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }*/


}
