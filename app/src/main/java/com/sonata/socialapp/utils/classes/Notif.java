package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
@ParseClassName("Notif")
public class Notif extends ParseObject {

    public SonataUser getOwner(){
        return (SonataUser) get("owner");
    }

    public String getPostId(){
        return getString("postid");
    }

    public String getType(){
        return getString("type");
    }

    public long getCount(){
        return getLong("count");
    }
    public ParseFile getMedia(){
        return getParseFile("mainmedia");
    }

    public String getDesc(){
        return getString("description");
    }

    public void setType(String type){
        put("type",type);
    }

    public boolean getNsfw(){
        return getBoolean("nsfw");
    }


    public String getCommentId(){
        return  getString("commentid");
    }


}
