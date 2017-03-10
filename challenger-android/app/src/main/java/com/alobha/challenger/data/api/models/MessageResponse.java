package com.alobha.challenger.data.api.models;

import java.io.Serializable;

/**
 * Created by mrNRG on 10.06.2016.
 */
public class MessageResponse implements Serializable {
    private String text;

    public MessageResponse(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
