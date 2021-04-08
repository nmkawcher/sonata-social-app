package com.sonata.socialapp.utils.classes;

import android.net.Uri;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {

    public String getType() {
        return getString("type") != null ? getString("type") : "";
    }

    public void setType(String type){
        put("type",type);
    }

    public String getMessage(){
        return getString("message");
    }

    public Uri getUri(){
        return Uri.parse(getString("uri"));
    }

    public void setMessage(String message){
        put("message",message);
    }

    public void setMedia(ParseFile media){
        put("media",media);
    }

    public void setIsUri(boolean isUri){
        put("isuri",isUri);
    }

    public boolean getIsUri(){
        return getBoolean("isuri");
    }

    public void setUri(Uri uri){
        put("uri",uri.toString());
    }

    public String getOwner(){
        return getString("owner");
    }

    public ParseFile getMedia(){
        return getParseFile("media");
    }

    public void setOwner(String owner){
        put("owner",owner);
    }

    public void setChat(String chat){
        put("chat",chat);
    }

}
