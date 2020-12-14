package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {

    public String getName(){
        return getString("name") != null ? getString("name") : "";
    }

    public String getDescription(){
        return getString("description") != null ? getString("description") : "";
    }

    public boolean getPrivate(){
        return getBoolean("private");
    }

    public long getMemberCount(){
        return getLong("membercount");
    }

    public List<String> getAdmins(){
        return getList("admins") != null ? getList("admins") : new ArrayList<>();
    }

    public String getImageUrl(){
        return getParseFile("image") != null ? getParseFile("image").getUrl() : "";
    }

    public String getOwner(){
        return getString("owner") != null ? getString("owner") : "";
    }

    public boolean getJoin(){
        if(getOwner().equals(ParseUser.getCurrentUser().getObjectId())||getAdmins().contains(ParseUser.getCurrentUser().getObjectId())){
            return true;
        }
        else{
            return getBoolean("join");
        }
    }

    public void setJoin(boolean join){
        put("join",join);
    }

    public boolean getJoin2(){
        if(getOwner().equals(ParseUser.getCurrentUser().getObjectId())
                ||getAdmins().contains(ParseUser.getCurrentUser().getObjectId())){
            return true;
        }
        else{
            return getBoolean("join2");
        }
    }

    public boolean getJoinRequest(){
        return getBoolean("joinrequest");
    }

    public boolean getJoinRequest2(){
        return getBoolean("joinrequest2");
    }

    public void setJoinRequest(boolean joinRequest){
        put("joinrequest",joinRequest);
    }

    public String getId(){
        return getObjectId() != null ? getObjectId() : "";
    }

    public long getShareType(){
        return getLong("shareType");
    }

}
