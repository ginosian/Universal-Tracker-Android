package com.margin.mgms.listener;

import android.support.annotation.CheckResult;
import android.support.annotation.StringRes;

import com.margin.mgms.R;

/**
 * A simple {@code String} validity checker.
 * <p>
 * Created on May 06, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface PinValidityChecker {

    @CheckResult
    boolean check(String pin);

    Reason getReason();

    enum Reason {
        OK(R.string.ok),
        WRONG_LENGTH(R.string.error_wrong_pin_length);

        @StringRes
        public int stringResId;

        Reason(@StringRes int stringResId) {
            this.stringResId = stringResId;
        }
    }
}