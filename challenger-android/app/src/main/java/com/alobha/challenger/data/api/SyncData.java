package com.alobha.challenger.data.api;

import java.util.ArrayList;

/**
 * Created by mrNRG on 28.06.2016.
 */
public class SyncData {

    public String token;
    public ArrayList<String> contacts;

    public SyncData() {
    }

    public SyncData(String token, ArrayList<String> contacts) {
        this.token = token;
        this.contacts = contacts;
    }
}
