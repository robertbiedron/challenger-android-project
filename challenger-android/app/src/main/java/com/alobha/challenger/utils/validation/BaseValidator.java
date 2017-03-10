package com.alobha.challenger.utils.validation;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by mrNRG on 10.06.2016.
 */
public abstract class BaseValidator implements TextWatcher {

    public abstract boolean isValid();

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isValid())
            clearErrors();
    }

    protected abstract void clearErrors();

    public abstract void destroy();
}
