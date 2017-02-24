package com.example.ciheul.baros.UserLevel;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Objects;

/**
 * define your user authorization for menu, layout, id or button action access
 * Created by ciheul on 24/02/17.
 */

public class UserLevel {

    // Superuser, can access all actions
    // personnel, can access all except edit another personnel, can comment, see their profiling, user statistic
    // people, can access case only, can comment
    // guest, can access case only

    int userType;

//    private Objects user = new Objects({});


    public UserLevel() {

    }

    public void menuDecorators() {
        // checking user menu authorization
        // check if user can access menu, layout or button, and put your logic here
    }


    ////////// page more //////////
    // arsip kasus,
    // ubah profile,
    // lihat statistic,
    // ganti password,
    // notification
    // logout

}
