package com.margin.mgms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Error response class, received from {@link com.margin.mgms.rest.StrongLoopApi StrongLoopApi}.
 * If the response mStatus mCode is not 200, that the mError body should be serialized to this class's object.
 * <p>
 * Typical mError response: <p>
 * <pre>
 * {
 *      "error": {
 *      "name": "Error",
 *      "status": 401,
 *      "message": "login failed",
 *      "statusCode": 401,
 *      "code": "LOGIN_FAILED",
 *      "stack": "Error: login failed"
 *      }
 * }
 * </pre>
 * <p>
 * <p>
 * Created on March 31, 2016.
 *
 * @author Marta.Ginosyan
 */
@SuppressWarnings("unused")
public class APIError {

    @SerializedName("error")
    public Error mError;

    public String getName() {
        return mError.getName();
    }

    public int getStatus() {
        return mError.getStatus();
    }

    public String getMessage() {
        return mError.getMessage();
    }

    public int getStatusCode() {
        return mError.getStatusCode();
    }

    public String getCode() {
        return mError.getCode();
    }

    public String getStack() {
        return mError.getStack();
    }

    public String getError() {
        return mError.getError();
    }

    @Override
    public String toString() {
        return "APIError{" +
                "Error=" + mError +
                '}';
    }

    private static class Error {

        @SerializedName("name")
        String mName;
        @SerializedName("status")
        int mStatus;
        @SerializedName("message")
        String mMessage;
        @SerializedName("statusCode")
        int mStatusCode;
        @SerializedName("code")
        String mCode;
        @SerializedName("stack")
        String mStack;
        @SerializedName("error")
        String mError;

        public String getName() {
            return mName;
        }

        public int getStatus() {
            return mStatus;
        }

        public String getMessage() {
            return mMessage;
        }

        public int getStatusCode() {
            return mStatusCode;
        }

        public String getCode() {
            return mCode;
        }

        public String getStack() {
            return mStack;
        }

        public String getError() {
            return mError;
        }

        @Override
        public String toString() {
            return "APIError{" +
                    "name='" + mName + '\'' +
                    ", status=" + mStatus +
                    ", message='" + mMessage + '\'' +
                    ", statusCode=" + mStatusCode +
                    ", code='" + mCode + '\'' +
                    ", stack='" + mStack + '\'' +
                    ", error='" + mError + '\'' +
                    '}';
        }
    }
}
