package com.sonata.socialapp.utils.classes;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SonataUser extends ParseUser {

    public static final long ACCOUNT_TYPE_PERSONAL = 1;
    public static final long ACCOUNT_TYPE_CONTENT_CREATOR = 2;

    public String getSearchText(){
        if(getString("searchText")!=null){
            return getString("searchText");
        }
        else{
            return "";
        }

    }

    public long getAccountType(){
        return getLong("accounttype");
    }
    public boolean getToMeFollowRequest2(){
        return getBoolean("tomefollowrequest2");
    }
    public boolean getFollowRequest2(){
        return getBoolean("followrequest2");
    }
    public boolean getBlock2(){
        return getBoolean("block2");
    }
    public boolean getFollow2(){
        return getBoolean("follow2");
    }

    public String getName(){
        if(getString("namesurname")!=null){
            return getString("namesurname");
        }
        else{
            return "";
        }
    }

    public List<String> getContent(){
        return getList("content") != null ? getList("content") : new ArrayList<>();
    }


    public boolean getPrivate(){
        return getBoolean("private");
    }

    public boolean getBlock(){
        return getBoolean("block");
    }

    public void setBlock(boolean block){
         put("block",block);
    }

    public boolean getFollowRequest(){
        return getBoolean("followrequest");
    }
    public boolean getToMeFollowRequest(){
        return getBoolean("tomefollowrequest");
    }

    public void setFollowRequest(boolean followrequest){
        put("followrequest",followrequest);
    }

    public void setToMeFollowRequest(boolean followrequest){
        put("tomefollowrequest",followrequest);
    }

    public boolean getFollow(){
        return getBoolean("follow");
    }

    public void setFollow(boolean block){
        put("follow",block);
    }

    public boolean getHasPp(){
        return getBoolean("haspp");
    }


    public String getPPbig(){
        if(getParseFile("profilephotoprofile")!=null){
            if(getParseFile("profilephotoprofile").getUrl()!=null){
                return getParseFile("profilephotoprofile").getUrl();
            }
            else{
                return "";
            }
        }
        else{
            return "";
        }

    }

    public ParseFile getAdapterPp(){
        return getParseFile("profilephotoadapter");
    }

    public ParseFile getBigPp(){
        return getParseFile("profilephotoprofile");
    }

    public String getPPAdapter(){
        if(getParseFile("profilephotoadapter")!=null){
            if(getParseFile("profilephotoadapter").getUrl()!=null){
                return getParseFile("profilephotoadapter").getUrl();
            }
            else{
                return "";
            }
        }
        else{
            return "";
        }

    }

    public long getFollowing(){
        return getLong("following");
    }

    public long getFollower(){
        return getLong("follower");
    }

    public long getFollowReqCount(){
        return getLong("followreqcount");
    }

    public void setFollowReqCount(long a){
        put("followreqcount",a);
    }

    public long getNotifCount(){
        return getLong("notifcount");
    }



    public String getBio(){
        if(getString("biography")!=null){
            return getString("biography").trim();
        }
        else{
            return "";
        }
    }

}

