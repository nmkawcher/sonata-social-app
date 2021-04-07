package com.sonata.socialapp.utils;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sonata.socialapp.utils.classes.Chat;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.Group;
import com.sonata.socialapp.utils.classes.GroupPost;
import com.sonata.socialapp.utils.classes.Message;
import com.sonata.socialapp.utils.classes.Notif;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;


public class MyApp extends Application {

    public static boolean ses = false;


    public static String whereAmI= "";

    public static final int TYPE_HASHTAG = 1;
    public static final int TYPE_LINK = 8;
    public static final int TYPE_MENTION = 2;


    @Override
    public void onCreate() {
        super.onCreate();
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        else{
            LeakCanary.install(this);
        }*/

        if(GenelUtil.getNightModeApp(this)){
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }




        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(GroupPost.class);
        ParseObject.registerSubclass(Chat.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Notif.class);
        ParseUser.registerSubclass(SonataUser.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fDVnuSxHVTycjWW2J3ZG9WHukxewXxZq")
                .enableLocalDataStore()
                .server("https://loadbalancer.sonatasocialapp.com/parse/")
                .build());

    }




}
