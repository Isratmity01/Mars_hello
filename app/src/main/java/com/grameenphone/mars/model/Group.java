package com.grameenphone.mars.model;


import java.util.ArrayList;
import java.util.Map;
import java.util.Map;

public class Group {

    public String groupId;
    public String name;
    public String owner;
    public String groupPic;
    public Map<String, Boolean> admin;
    public Map<String, Boolean> member;

    public Group() {}

    public Group(String groupid, String name, String owner, Map<String, Boolean> admin, Map<String, Boolean> member, String groupPic) {
        this.groupId = groupid;
        this.name = name;
        this.admin = admin;
        this.member = member;
        this.owner = owner;
        this.groupPic = groupPic;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getAdmin() {
        return admin;
    }

    public void setAdmin(Map<String, Boolean> admin) {
        this.admin = admin;
    }

    public Map<String, Boolean> getMember() {
        return member;
    }

    public void setMember(Map<String, Boolean> member) {
        this.member = member;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroupPic() {
        return groupPic;
    }

    public void setGroupPic(String groupPic) {
        this.groupPic = groupPic;
    }
}
