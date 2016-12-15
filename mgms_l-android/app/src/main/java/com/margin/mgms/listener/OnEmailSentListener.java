package com.margin.mgms.listener;

import com.margin.mgms.model.APIError;

/**
 * Listener interface for checking the status on sending emails.
 * <p>
 * Created on Jun 23, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnEmailSentListener {

    /**
     * The email was successfully sent.
     */
    void onEmailSentSuccess();

    /**
     * The email sending failed with {@link APIError} error
     */
    void onEmailSentError(APIError error);

    /**
     * The email sending failed with {@link Throwable} error
     */
    void onEmailSentFailure(Throwable throwable);
}
