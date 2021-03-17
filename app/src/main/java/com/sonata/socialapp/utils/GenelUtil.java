package com.sonata.socialapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.CommentActivity;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.activities.sonata.HashtagActivity;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.AdListener;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.listeners.OnDismissListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.parse.Parse.getApplicationContext;


public class GenelUtil {

    public static Comment comment = null;
    private static long a;
    public static final int ACCOUNT_LIMIT = 5;

    public static int TYPE_POST = 123;
    public static int TYPE_COMMENT = 124;

    public static String appUrl = "sonatasocialapp.com";




    public static void loadAds(int l,Context context, AdListener listener){
        int c = 0;
        if(l>1&&l<=11){
            c=1;
        }
        if(l>11&&l<=21){
            c=2;
        }
        if(l>21&&l<=31){
            c=3;
        }
        if(l>31&&l<=41){
            c=4;
        }
        if(l>41){
            c=5;
        }
        AtomicBoolean isfinish = new AtomicBoolean(false);
        List<UnifiedNativeAd> tempList = new ArrayList<>();
        final int[] loadCheck = {0};
        int finalC = c;
        if(c>0){
            for(int i = 0; i < c; i++){
                AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.adId))
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                loadCheck[0]++;
                                tempList.add(unifiedNativeAd);
                                if(loadCheck[0] == finalC){
                                    isfinish.set(true);
                                    listener.done(tempList);

                                }
                            }
                        })
                        .withAdListener(new com.google.android.gms.ads.AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                Log.e("Ad Error Code:",adError.getCode()+"");
                                Log.e("Ad Error Code:",adError.getMessage()+"");
                                loadCheck[0]++;
                                if(loadCheck[0] == finalC){
                                    isfinish.set(true);
                                    listener.done(tempList);

                                }
                            }
                        }).build();


                adLoader.loadAd(new AdRequest.Builder().build());
            }

            Log.e("Delay Öncesi zaman : ",System.currentTimeMillis()+"");
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!isfinish.get()){
                        isfinish.set(true);
                        listener.done(tempList);
                    }
                }
            }, Math.max(c * 4500, 7000));
        }
        else{
            listener.done(new ArrayList<>());
        }

    }

    public static List<String> getSeenList(Context context){

        SharedPreferences pref = context.getSharedPreferences("seenList", 0);
        String userArrayStr = pref.getString("list","");
        if(userArrayStr != null && userArrayStr.length()>0){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(userArrayStr);
                String ls = jsonObject.getString("seenList");
                return new ArrayList<String>(Arrays.asList(ls.replace("[","").replace("]","").split(", ")));
                /*List<String> res = new ArrayList<>();
                for(int i = 0; i < tmpArray.length(); i++){
                    res.add(tmpArray.getString(i));
                }
                return res;*/

            } catch (Exception e) {
                //ToastLong(e.getMessage(),context);
                e.printStackTrace();
                Log.e("SeenList",e.getMessage());
            }
            return new ArrayList<>();
        }
        else{
            return new ArrayList<>();
        }
    }

    public static void setSeenList(Context context,List<String> array){

        SharedPreferences pref = context.getSharedPreferences("seenList", 0);


        try {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("seenList",array.subList(Math.max(array.size()-1000,0),array.size()));
            SharedPreferences.Editor editor = pref.edit();

            editor.putString("list",jsonObject2.toString());
            editor.apply();
        } catch (JSONException e) {
            Log.e("saveUser",e.toString());
            e.printStackTrace();
        }

    }

    public static String getUrlOfObject(ParseObject object){
        if(object.getClassName().equals("_User")){
            return "https://www.sonatasocialapp.com/user/"+((ParseUser)object).getUsername();
        }
        else if(object.getClassName().equals("Post")){
            return "https://www.sonatasocialapp.com/post/"+object.getObjectId();
        }
        return "";
    }

    public static void handlePostOptionsClick(Context context, int position, List<ListObject> list, RecyclerView.Adapter adapter, TextView commentNumber){
        Post post = list.get(position).getPost();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        ArrayList<String> other = new ArrayList<>();
        if(post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi benim
            if(post.getCommentable()){
                other.add(context.getString(R.string.disablecomment));
            }
            if(!post.getCommentable()){
                other.add(context.getString(R.string.enablecomment));
            }
            other.add(context.getString(R.string.delete));

        }
        if(post.getSaved()){
            other.add(context.getString(R.string.unsavepost));
        }
        if(!post.getSaved()){
            other.add(context.getString(R.string.savepost));
        }
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi başkasına ait
            other.add(context.getString(R.string.report));
        }
        other.add(context.getString(R.string.copylink));
        other.add(context.getString(R.string.share));

        String[] popupMenu = other.toArray(new String[other.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);

        builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String select = popupMenu[which];
                if(select.equals(context.getString(R.string.disablecomment))){

                    progressDialog.setMessage(context.getString(R.string.disablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(false);
                                progressDialog.dismiss();
                                commentNumber.setText(context.getString(R.string.disabledcomment));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });

                }
                else if(select.equals(context.getString(R.string.savepost))){
                    //savePost

                    progressDialog.setMessage(context.getString(R.string.savepost));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("savePost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(true);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.postsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });

                }
                else if(select.equals(context.getString(R.string.unsavepost))){
                    //UnsavePost

                    progressDialog.setMessage(context.getString(R.string.unsavepost));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unsavePost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(false);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.postunsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });
                }
                else if(select.equals(context.getString(R.string.report))){

                    progressDialog.setMessage(context.getString(R.string.report));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("reportPost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                GenelUtil.ToastLong(context,context.getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });


                }
                else if(select.equals(context.getString(R.string.enablecomment))){

                    progressDialog.setMessage(context.getString(R.string.enablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(true);
                                progressDialog.dismiss();

                                commentNumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),context));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });

                }
                else if(select.equals(context.getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.deletetitle);
                    builder.setCancelable(true);
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            progressDialog.setMessage(context.getString(R.string.delete));
                            progressDialog.show();
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("postID", post.getObjectId());
                            ParseCloud.callFunctionInBackground("deletePost", params, new FunctionCallback<String>() {
                                @Override
                                public void done(String object, ParseException e) {
                                    if(e==null&&object.equals("deleted")){
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position,list.size());
                                        progressDialog.dismiss();

                                    }
                                    else{
                                        progressDialog.dismiss();
                                        GenelUtil.ToastLong(context,context.getString(R.string.error));
                                    }
                                }
                            });


                        }
                    });
                    builder.show();

                }
                else if(select.equals(context.getString(R.string.share))){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getUrlOfObject(post));
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);
                }
                else if(select.equals(context.getString(R.string.copylink))){
                    copyText(getUrlOfObject(post),context);
                }
            }
        });
        builder.show();
    }

    public static void handlePostOptionsClickInComments(Activity context, Post post, TextView commentNumber){


        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        ArrayList<String> other = new ArrayList<>();
        if(post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi benim
            if(post.getCommentable()){
                other.add(context.getString(R.string.disablecomment));
            }
            if(!post.getCommentable()){
                other.add(context.getString(R.string.enablecomment));
            }
            other.add(context.getString(R.string.delete));

        }
        if(post.getSaved()){
            other.add(context.getString(R.string.unsavepost));
        }
        if(!post.getSaved()){
            other.add(context.getString(R.string.savepost));
        }
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi başkasına ait
            other.add(context.getString(R.string.report));
        }
        other.add(context.getString(R.string.copylink));
        other.add(context.getString(R.string.share));

        String[] popupMenu = other.toArray(new String[other.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);

        builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String select = popupMenu[which];
                if(select.equals(context.getString(R.string.disablecomment))){

                    progressDialog.setMessage(context.getString(R.string.disablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(false);
                                progressDialog.dismiss();
                                commentNumber.setText(context.getString(R.string.disabledcomment));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });

                }
                else if(select.equals(context.getString(R.string.savepost))){
                    //savePost

                    progressDialog.setMessage(context.getString(R.string.savepost));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("savePost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(true);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.postsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });

                }
                else if(select.equals(context.getString(R.string.unsavepost))){
                    //UnsavePost

                    progressDialog.setMessage(context.getString(R.string.unsavepost));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unsavePost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(false);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.postunsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });
                }
                else if(select.equals(context.getString(R.string.report))){

                    progressDialog.setMessage(context.getString(R.string.report));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("reportPost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                GenelUtil.ToastLong(context,context.getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });


                }
                else if(select.equals(context.getString(R.string.enablecomment))){

                    progressDialog.setMessage(context.getString(R.string.enablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(true);
                                progressDialog.dismiss();

                                commentNumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),context));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(context,context.getString(R.string.error));
                            }
                        }
                    });

                }
                else if(select.equals(context.getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.deletetitle);
                    builder.setCancelable(true);
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            progressDialog.setMessage(context.getString(R.string.delete));
                            progressDialog.show();
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("postID", post.getObjectId());
                            ParseCloud.callFunctionInBackground("deletePost", params, new FunctionCallback<String>() {
                                @Override
                                public void done(String object, ParseException e) {
                                    if(e==null&&object.equals("deleted")){
                                        post.setIsDeleted(true);
                                        context.finish();

                                    }
                                    else{
                                        progressDialog.dismiss();
                                        GenelUtil.ToastLong(context,context.getString(R.string.error));
                                    }
                                }
                            });


                        }
                    });
                    builder.show();

                }
                else if(select.equals(context.getString(R.string.share))){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getUrlOfObject(post));
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);
                }
                else if(select.equals(context.getString(R.string.copylink))){
                    copyText(getUrlOfObject(post),context);
                }
            }
        });
        builder.show();
    }


    public static void handleLinkClicks(Context context, String text,int clickType){
        if(clickType== MyApp.TYPE_HASHTAG){
            //hashtag
            context.startActivity(new Intent(context, HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==MyApp.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                context.startActivity(new Intent(context, GuestProfileActivity.class).putExtra("username",username));
            }

        }
        else if(clickType==MyApp.TYPE_LINK){
            if(text.contains(appUrl)){
                String newS = text.substring(text.indexOf(appUrl)+appUrl.length());
                if(newS.startsWith("/")){
                    newS = newS.substring(1);
                }
                Log.e("String deneme", newS);
                Log.e("String deneme", newS.indexOf("anan")+"");
                if(newS.startsWith("post/")){
                    newS = newS.replace("post/","");
                    context.startActivity(new Intent(context, CommentActivity.class).putExtra("id",newS));
                }
                else if(newS.startsWith("user/")){
                    newS = newS.replace("user/","");
                    if(!newS.equals(ParseUser.getCurrentUser().getUsername())){
                        context.startActivity(new Intent(context, GuestProfileActivity.class).putExtra("username",newS));
                    }
                }
                else{
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    String url = text;
                    if(!url.startsWith("http")){
                        url = "http://"+url;
                    }
                    if(GenelUtil.getNightMode()){
                        builder.setToolbarColor(Color.parseColor("#303030"));
                    }
                    else{
                        builder.setToolbarColor(Color.parseColor("#ffffff"));
                    }
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(context, Uri.parse(url));
                }
            }
            else{
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                String url = text;
                if(!url.startsWith("http")){
                    url = "http://"+url;
                }
                if(GenelUtil.getNightMode()){
                    builder.setToolbarColor(Color.parseColor("#303030"));
                }
                else{
                    builder.setToolbarColor(Color.parseColor("#ffffff"));
                }
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(url));
            }
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



    public static byte[] getBytesFromBitmap(Bitmap bm){

        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }catch (Exception e){
            return new byte[0];
        }

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



    public static void copy(Uri uri, File dst, Context context, ProgressDialog progressDialog) throws IOException {
        InputStream in = context.getContentResolver().openInputStream(uri);
        try {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                assert in != null;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (Exception e) {
                progressDialog.dismiss();
                ToastLong(context.getString(R.string.unsupportedvideo), context);
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
                return pref.getBoolean("nightMode",true);
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
                return pref.getBoolean("nightMode",true);
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


    public static void showKeyboard(Activity activity){
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.toggleSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception ignored){

        }
    }

    public static void hideKeyboard(Activity activity) {
        try{
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored){

        }

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
        try{
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(context.getString(R.string.app_name), text);
            clipboard.setPrimaryClip(clip);
            ToastLong(context,context.getString(R.string.linkcopied));
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

}
