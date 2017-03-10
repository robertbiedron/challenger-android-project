package com.alobha.challenger.utils.validation;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

/**
 * Created by mrNRG on 10.06.2016.
 */

public class EmailValidator extends BaseValidator {
    private EditText etEmail;

    public EmailValidator(EditText email) {
        this.etEmail = email;
        etEmail.addTextChangedListener(this);
    }

    public boolean isValid() {
        String email = etEmail.getText().toString();
        boolean matches = Patterns.EMAIL_ADDRESS.matcher(email).matches() || !etEmail.isEnabled();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email should not be empty");
        } else if (!matches)
            etEmail.setError("Please, provide correct email");
        return matches;
    }

    @Override
    protected void clearErrors() {
        etEmail.setError(null);
    }

    @Override
    public void destroy() {
        etEmail.removeTextChangedListener(this);
        etEmail = null;
    }
}
