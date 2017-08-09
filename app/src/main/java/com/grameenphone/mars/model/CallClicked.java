package com.grameenphone.mars.model;

/**
 * Created by HP on 8/9/2017.
 */

public class CallClicked {
    private String uid;
    private String name;

    public CallClicked() {
    }

    public CallClicked(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
