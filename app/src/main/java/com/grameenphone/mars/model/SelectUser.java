package com.grameenphone.mars.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by shadman.rahman on 11-Jun-17.
 */

public class SelectUser implements Comparable<SelectUser>{
    String name;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    String thumb;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String phone;

    public Boolean getCheckedBox() {
        return checkedBox;
    }

    public void setCheckedBox(Boolean checkedBox) {
        this.checkedBox = checkedBox;
    }

    Boolean checkedBox = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static Comparator<SelectUser> NameComparator = new Comparator<SelectUser>() {

        public int compare(SelectUser s1, SelectUser s2) {
            String StudentName1 = s1.getName().toUpperCase();
            String StudentName2 = s2.getName().toUpperCase();

            //ascending order
            return StudentName1.compareTo(StudentName2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};

    @Override
    public int compareTo(@NonNull SelectUser o) {
        return name.compareTo(o.name);
    }
}