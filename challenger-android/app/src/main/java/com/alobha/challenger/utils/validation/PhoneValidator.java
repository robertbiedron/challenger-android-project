package com.alobha.challenger.utils.validation;

import android.telephony.PhoneNumberUtils;
import android.widget.EditText;

/**
 * Created by mrNRG on 10.06.2016.
 */

public class PhoneValidator extends BaseValidator {
    private EditText etPhone;

    public PhoneValidator(EditText email) {
        this.etPhone = email;
        etPhone.addTextChangedListener(this);
    }

    public boolean isValid() {
        String phone = etPhone.getText().toString();
        boolean matches = PhoneNumberUtils.isGlobalPhoneNumber(phone) && phone.length() >= 12 && phone.length() <= 13
                || !etPhone.isEnabled();
        if (!matches)
            etPhone.setError("Please, provide correct phone number\n" +
                    "in format +1 234 567 8900");
        return matches;
    }

    @Override
    protected void clearErrors() {
        etPhone.setError(null);
    }

    @Override
    public void destroy() {
        etPhone.removeTextChangedListener(this);
        etPhone = null;
    }
}
