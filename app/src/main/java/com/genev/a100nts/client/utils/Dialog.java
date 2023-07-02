package com.genev.a100nts.client.utils;

import android.app.AlertDialog;
import android.content.Context;

public final class Dialog {

    private Dialog() {
    }

    public static void showInfoDialog(Context context, Integer titleId, Object message) {
        showDialog(context, titleId, message, android.R.drawable.ic_dialog_info);
    }

    public static void showWarningDialog(Context context, Integer titleId, Object message) {
        showDialog(context, titleId, message, android.R.drawable.ic_dialog_alert);
    }

    private static void showDialog(Context context, Integer titleId, Object message, int iconId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setIcon(iconId);
        if (titleId != null) {
            dialogBuilder = dialogBuilder.setTitle(titleId);
        }
        if (message instanceof Integer) {
            dialogBuilder = dialogBuilder.setMessage(((int) message));
        } else {
            dialogBuilder = dialogBuilder.setMessage(String.valueOf(message));
        }
        dialogBuilder.show();
    }

}
