package com.grameenphone.mars.model;

/**
 * Created by shadman.rahman on 05-Jun-17.
 */

public class UserCalls {
    private String UserName;
    private int is_me;

    public UserCalls(String userName, int is_me) {
        UserName = userName;
        this.is_me = is_me;
    }

    public UserCalls() {
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getIs_me() {
        return is_me;
    }

    public void setIs_me(int is_me) {
        this.is_me = is_me;
    }
}
