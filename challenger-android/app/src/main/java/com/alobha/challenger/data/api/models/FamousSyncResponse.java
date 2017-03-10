package com.alobha.challenger.data.api.models;


import com.alobha.challenger.data.entities.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class FamousSyncResponse extends StatusResponse implements Serializable {
    public List<User> famous;
}
