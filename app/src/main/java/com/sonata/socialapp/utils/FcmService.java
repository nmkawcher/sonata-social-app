package com.sonata.socialapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.ParseCloud;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.CommentActivity;
import com.sonata.socialapp.activities.sonata.FollowRequestActivity;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class FcmService extends FirebaseMessagingService {

    NotificationManager notificationManager;
    final static  String ADMIN_CHANNEL_ID = "channel";
    final static int followedyou = 254879;
    final static int followrequest = 354459;
    final static int acceptfollowrequest = 457849;
    final static int mentionPost = 557849;
    final static int likePost = 654149;
    final static int commentPost = 765250;
    final static int mentionComment = 876261;
    final static int replyComment = 987372;
    final static int uploadComplete = 198483;







    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        HashMap<String,String> params = new HashMap<>();
        params.put("token",s);
        ParseCloud.callFunctionInBackground("updateToken",params);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(ParseUser.getCurrentUser()!=null){
            if(remoteMessage.getData().get("to")!=null){
                if(remoteMessage.getData().get("to").equals(ParseUser.getCurrentUser().getObjectId())){

                    if(remoteMessage.getData().get("type")!=null){


                        if(remoteMessage.getData().get("type").equals("followedyou")){

                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.followedyou);
                            Intent intent = new Intent(this, GuestProfileActivity.class);
                            intent.putExtra("username",remoteMessage.getData().get("username"));
                            intent.putExtra("notif",true);

                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle( new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(followedyou, notificationBuilder.build());
                        }
                        else if(remoteMessage.getData().get("type").equals("followrequest")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.followrequest);
                            Intent intent = new Intent(this, FollowRequestActivity.class);
                            intent.putExtra("notif",true);

                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(followrequest, notificationBuilder.build());
                        }
                        else if(remoteMessage.getData().get("type").equals("acceptedfollowrequest")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.acceptfollowrequest);
                            Intent intent = new Intent(this, GuestProfileActivity.class);
                            intent.putExtra("username",remoteMessage.getData().get("username"));
                            intent.putExtra("notif",true);

                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(acceptfollowrequest, notificationBuilder.build());
                        }
                        else if(remoteMessage.getData().get("type").equals("mentionpost")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.mentionpost);
                            Intent intent = new Intent(this, CommentActivity.class);
                            intent.putExtra("notif",true);
                            intent.putExtra("id",remoteMessage.getData().get("postid"));


                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(mentionPost, notificationBuilder.build());
                        }
                        else if(remoteMessage.getData().get("type").equals("likedpost")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.likedyourpost);
                            Intent intent = new Intent(this, CommentActivity.class);
                            intent.putExtra("id",remoteMessage.getData().get("postid"));
                            intent.putExtra("notif",true);



                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(likePost, notificationBuilder.build());

                        }
                        else if(remoteMessage.getData().get("type").equals("commentpost")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.commentyourpost);
                            Intent intent = new Intent(this, CommentActivity.class);
                            intent.putExtra("notif",true);
                            intent.putExtra("commentid",remoteMessage.getData().get("commentid"));


                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(commentPost, notificationBuilder.build());

                        }
                        else if(remoteMessage.getData().get("type").equals("mentioncomment")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.mentioncomment);
                            Intent intent = new Intent(this, CommentActivity.class);
                            intent.putExtra("notif",true);
                            intent.putExtra("commentid",remoteMessage.getData().get("commentid"));


                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(mentionComment, notificationBuilder.build());

                        }

                        else if(remoteMessage.getData().get("type").equals("replycomment")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.replycomment);
                            Intent intent = new Intent(this, CommentActivity.class);
                            intent.putExtra("notif",true);
                            intent.putExtra("commentid",remoteMessage.getData().get("commentid"));


                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                            Bitmap bitmap = getBitmap(remoteMessage.getData().get("image")); //obtain the image
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setLargeIcon(bitmap)//a resource for your custom small icon
                                    .setContentTitle(getString(R.string.app_name)) //the "title" value you sent in your notification
                                    .setContentText(remoteMessage.getData().get("name")+" "+title) //ditto
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("name")+" "+title));
                            notificationManager.notify(replyComment, notificationBuilder.build());

                        }

                        else if(remoteMessage.getData().get("type").equals("postUploadComplete")){
                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                setupChannels();
                            }
                            String title=getString(R.string.uplcompt);
                            Intent intent = new Intent(this, CommentActivity.class);
                            intent.setAction(Long.toString(System.currentTimeMillis()));
                            intent.putExtra("id",remoteMessage.getData().get("postid"));


                            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

                            Bitmap bitmap = getBitmapPost(remoteMessage.getData().get("image")); //obtain the image

                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,ADMIN_CHANNEL_ID )
                                    .setSmallIcon(R.drawable.ic_new_notification_icon)
                                    .setColor(getResources().getColor(R.color.notif))
                                    .setContentTitle(getString(R.string.uplcompttitle)) //the "title" value you sent in your notification
                                    .setContentText(title) //ditto
                                    .setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)//dismisses the notification on click
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title));
                            Random rand = new Random(); //instance of random class

                            int int_random = rand.nextInt(100000);
                            notificationManager.notify(int_random, notificationBuilder.build());

                        }
                    }

                }
            }
        }



    }

    private Bitmap getBitmap(String url){
        Bitmap bitmap = null;
        try {
            if(url.equals("empty")){
                bitmap = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(R.drawable.emptypp)
                        .apply(RequestOptions.circleCropTransform())
                        .submit(200,200)
                        .get();
                return bitmap;

            }
            else{
                bitmap = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .submit(200,200)
                        .get();
                return bitmap;
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private Bitmap getBitmapPost(String url){
        Bitmap bitmap = null;
        try {
                if(url!=null){
                    bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(url)
                            .submit(200,200)
                            .get();
                    return bitmap;
                }
                else{
                    return bitmap;
                }



        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return bitmap;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}
