package com.example.mytest.sharedclass;

import com.google.gson.Gson;

public class Shared_class {

    Gson gson = new Gson();

    public Shared_class(){

    }
    /////////////////////////////////////////////////
    public Shared_user fromuser(String user){

        Shared_user shared_user = gson.fromJson(user, Shared_user.class);
        return shared_user;
    }
    public String touser(Shared_user shared_user){

        String tojson = gson.toJson(shared_user);
        return tojson;
    }
    /////////////////////////////////////////////////
    public Shared_Group fromGroup(String group){

        Shared_Group shared_group = gson.fromJson(group, Shared_Group.class);
        return shared_group;
    }
    public String togroup(Shared_Group shared_group){

        String tojson = gson.toJson(shared_group);
        return tojson;
    }
    /////////////////////////////////////////////////
    public Shared_Studies fromstudies(String studies){

        Shared_Studies shared_studies = gson.fromJson(studies, Shared_Studies.class);
        return shared_studies;
    }
    public String tostudies(Shared_Studies shared_studies){

        String tojson = gson.toJson(shared_studies);
        return tojson;
    }

    public String totransmail(String mail){
        return mail = mail.replace(".","_");
    }
    public String fromtransmail(String mail){
        return mail = mail.replace("_",".");
    }

}
