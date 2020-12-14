package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("GroupPost")
public class GroupPost extends ParseObject {


    public List<String> getExcludeList(){
        return getList("excludelist") != null ? getList("excludelist") : new ArrayList<>();
    }

    public void setSaved(boolean bool){
        put("saved",bool);
    }

    public boolean getNsfw(){
        return getBoolean("nsfw");
    }
    public boolean getSaved(){
        return getBoolean("saved");
    }
    public boolean getSaved2(){
        return getBoolean("saved2");
    }


    public boolean getUpvote2(){
        return getBoolean("upvote2");
    }

    public boolean getUpvote(){
        return getBoolean("upvote");
    }

    public void setUpvote(boolean bool){
        put("upvote",bool);
    }

    public boolean getDownvote2(){
        return getBoolean("downvote2");
    }

    public void setDownvote(boolean bool){
        put("downvote",bool);
    }

    public boolean getDownvote(){
        return getBoolean("downvote");
    }

    public long getVote2(){ return getLong("votenumber2"); }

    public void setVote(long a){
        put("votenumber",a);
    }

    public long getVote(){
        return getLong("votenumber");
    }


    public long getCommentnumber() {
        return getLong("commentnumber");
    }
    public long getCommentnumber2() {
        return getLong("commentnumber2");
    }

    public long getImageCount() {
        return getLong("imagecount");
    }



    public ParseFile getMainMedia() {
        return getParseFile("mainmedia");
    }
    public ParseFile getMainMedia1() {
        return getParseFile("mainmedia1");
    }
    public ParseFile getMainMedia2() {
        return getParseFile("mainmedia2");
    }
    public ParseFile getMainMedia3() {
        return getParseFile("mainmedia3");
    }


    public ParseFile getThumbMedia() {
        return getParseFile("thumbmedia");
    }
    public ParseFile getThumbMedia2() {
        return getParseFile("thumbmedia2");
    }

    public ParseFile getThumbMedia1() {
        return getParseFile("thumbmedia1");
    }
    public ParseFile getThumbMedia3() {
        return getParseFile("thumbmedia3");
    }


    public void setCommentnumber(long a) {
        put("commentnumber",a);
    }
    public void setCommentable(boolean a) {
        put("commentable",a);
    }

    public void setPinned(boolean a) {
        put("pinned",a);
    }

    public boolean getPinned(){
        return getBoolean("pinned");
    }

    public boolean getCommentable() {
        return getBoolean("commentable");
    }
    public boolean getCommentable2() {
        return getBoolean("commentable2");
    }






    public void setUser(ParseUser user){
        put("user",user);
    }



    public SonataUser getUser() {
        return (SonataUser)get("user");
    }

    public String getUrl() {
        return getString("url");
    }

    public void setUrl(String url) {
        put("url",url);
    }

    public void setLinktitle(String linktitle) {
        put("linktitle",linktitle);
    }

    public String getLinktitle() {
        return getString("linktitle");
    }


    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String e) {
        put("description",e);
    }

    public int getRatioW() {
        return (int)get("ratiow");
    }


    public int getRatioH() {
        return (int)get("ratioh");
    }





    public String getLinkimageurl() {
        return getString("linkimageurl");
    }

    public void setLinkdesc(String linkdesc) {
        put("linkdesc",linkdesc);
    }

    public String getLinkdesc() {
        return getString("linkdesc");
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
