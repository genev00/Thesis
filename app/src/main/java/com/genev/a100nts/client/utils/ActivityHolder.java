package com.genev.a100nts.client.utils;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.ui.login.LoginActivity;

public final class ActivityHolder {

    private static AppCompatActivity activity;

    private ActivityHolder() {
    }

    public static AppCompatActivity getActivity() {
        return activity;
    }

    public static void setActivity(AppCompatActivity activity) {
        ActivityHolder.activity = activity;
    }

    public static void notifyLoginFailed(String email) {
        if (activity instanceof LoginActivity) {
            ((LoginActivity) activity).loginFailed(email);
        }
    }

}
