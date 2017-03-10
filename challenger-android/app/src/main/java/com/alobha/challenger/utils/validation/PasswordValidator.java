package com.alobha.challenger.utils.validation;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import org.passay.IllegalCharacterRule;
import org.passay.PasswordData;

/**
 * Created by mrNRG on 10.06.2016.
 */

public class PasswordValidator extends BaseValidator {
    public static final char[] illegalChars = new char[]{'%', '@', '&', '/', '#', '№', ';', '\'', '\\', '\"', '(', ')', '*', '[', ']'};
    public static final IllegalCharacterRule passwordRule = new IllegalCharacterRule(illegalChars);
    private EditText etPassword;
    private EditText etPassword2;

    public PasswordValidator(EditText password, @Nullable EditText password2) {
        this.etPassword = password;
        etPassword.addTextChangedListener(this);
        this.etPassword2 = password2;
        if (password2 != null)
            etPassword2.addTextChangedListener(this);
    }

    public boolean isValid() {
        String password = etPassword.getText().toString();
        String password2 = etPassword2 == null ? null : etPassword2.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password should not be empty");
            return false;
        } else if (password.length() < 6 || password.length() > 20) {
            etPassword.setError("Password should be at least 6 characters and less than 20");
            return false;
        } else if (!passwordRule.validate(new PasswordData(password)).isValid()) {
            etPassword.setError(getIllegalCharMessage());
            return false;
        } else if (password2 != null && !password.equals(password2)) {
            etPassword2.setError("Passwords should be the same");
            return false;
        }
        return true;
    }

    public String getIllegalCharMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(illegalChars[0]);
        for (int i = 1; i < illegalChars.length; i++) {
            sb.append(", ");
            sb.append(illegalChars[i]);
        }
        return String.format("Password should not contain characters: %s", sb.toString());
    }

    @Override
    protected void clearErrors() {
        etPassword.setError(null);
        if (etPassword2 != null)
            etPassword2.setError(null);
    }

    @Override
    public void destroy() {
        etPassword.removeTextChangedListener(this);
        if (etPassword2 != null) {
            etPassword2.removeTextChangedListener(this);
        }
        etPassword = null;
        etPassword2 = null;
    }
}
