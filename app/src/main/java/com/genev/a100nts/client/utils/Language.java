package com.genev.a100nts.client.utils;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.genev.a100nts.client.ui.login.LoginActivity;

import java.util.Locale;

public final class Language {

    private Language() {
    }

    public static String getCurrent() {
        return ActivityHolder.getActivity().getResources().getConfiguration().getLocales().get(0).getLanguage();
    }

    public static boolean isCurrentEnglish() {
        return Locale.ENGLISH.getLanguage().equals(getCurrent());
    }

    public static void changeLanguage() {
        final AppCompatActivity currentActivity = ActivityHolder.getActivity();
        String newLanguage = isCurrentEnglish() ? "bg" : Locale.ENGLISH.getLanguage();
        Resources resources = currentActivity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(newLanguage));
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        Intent login = new Intent(currentActivity, LoginActivity.class);
        currentActivity.startActivity(login);
        currentActivity.finish();
    }

}
