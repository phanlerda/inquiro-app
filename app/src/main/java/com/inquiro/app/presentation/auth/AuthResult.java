package com.inquiro.app.presentation.auth;

import androidx.annotation.Nullable;

public class AuthResult {
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    public final Status status;
    @Nullable
    public final String errorMessage;

    private AuthResult(Status status, @Nullable String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public static AuthResult success() {
        return new AuthResult(Status.SUCCESS, null);
    }

    public static AuthResult error(String message) {
        return new AuthResult(Status.ERROR, message);
    }

    public static AuthResult loading() {
        return new AuthResult(Status.LOADING, null);
    }
}