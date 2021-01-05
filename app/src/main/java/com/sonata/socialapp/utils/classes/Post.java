package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {




    public void setSaved(boolean bool){
        put("saved",bool);
    }

    public boolean getSaved(){
        return getBoolean("saved");
    }
    public boolean getSaved2(){
        return getBoolean("saved2");
    }

    public void setLiked(boolean bool){
        put("liked",bool);
    }

    public boolean getLiked(){
        return getBoolean("liked");
    }
    public boolean getLiked2(){
        return getBoolean("liked2");
    }

    public long getCommentnumber() {
        return getLong("commentnumber");
    }
    public long getCommentnumber2() {
        return getLong("commentnumber2");
    }

    public long getImageCount() {
        return getList("media") != null ? getList("media").size():1;
    }



    public List<HashMap> getMediaList(){
        return getList("media");
    }

    public void setCommentnumber(long a) {
        put("commentnumber",a);
    }
    public void setLikenumber(long a) {
        put("likenumber",a);
    }
    public void setCommentable(boolean a) {
        put("commentable",a);
    }



    public boolean getCommentable() {
        return getBoolean("commentable");
    }
    public boolean getCommentable2() {
        return getBoolean("commentable2");
    }


    public long getLikenumber() {
        return getLong("likenumber");
    }
    public long getLikenumber2() {
        return getLong("likenumber2");
    }




    public void setUser(ParseUser user){
        put("user",user);
    }



    public SonataUser getUser() {
        return (SonataUser)get("user");
    }




    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String e) {
        put("description",e);
    }



    public void setType(String type) {
        put("type",type);
    }

    public String getType() {
        return getString("type");
    }


    public void setIsDeleted(boolean type) {
        put("deleted",type);
    }

    public boolean getIsDeleted() {
        return getBoolean("deleted");
    }






}
