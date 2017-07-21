package io.keepcube.kcapp.Tools;

import android.support.design.widget.Snackbar;
import android.view.View;

public class Snacker {
    public static final int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
    public static final int LENGTH_LONG = Snackbar.LENGTH_LONG;
    public static final int LENGTH_INDEFINITE = Snackbar.LENGTH_INDEFINITE;
    private static Snackbar snack = null;

    public static Snackbar make(View v, String message, int length) {
        snack = Snackbar.make(v, message, length);
        return snack;
    }

    public static void dismiss() {
        if (Snacker.isShown()) snack.dismiss();
    }

    public static int getHeight() {
        return Snacker.isShown() ? Snacker.snack.getView().getHeight() : 0;
    }

    public static boolean isShown() {
        return Snacker.snack != null && Snacker.snack.isShown();
    }

}
