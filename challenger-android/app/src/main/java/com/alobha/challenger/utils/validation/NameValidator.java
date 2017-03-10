package com.alobha.challenger.utils.validation;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by mrNRG on 10.06.2016.
 */

public class NameValidator extends BaseValidator {
    private EditText editText;

    public NameValidator(EditText email) {
        this.editText = email;
        editText.addTextChangedListener(this);
    }

    public boolean isValid() {
        String text = editText.getText().toString();
        boolean matches = ((!TextUtils.isEmpty(text)) && TextUtils.getTrimmedLength(text) > 0) || !editText.isEnabled();
        if (!matches)
            editText.setError("Name should not be empty");
        return matches;
    }


    @Override
    protected void clearErrors() {
        editText.setError(null);
    }

    @Override
    public void destroy() {
        editText.removeTextChangedListener(this);
        editText = null;
    }
}
