package com.alobha.challenger.data.api.models;

import com.alobha.challenger.data.entities.User;

import java.io.Serializable;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class AnonymousResponse  extends StatusResponse implements Serializable {
    public String token;
    public User anonymous;
}
