package tk.foodpedia.android.util;

import android.support.annotation.StringRes;
import android.widget.Toast;

import tk.foodpedia.android.App;

public final class ToastHelper {
    private ToastHelper() { /* */ }

    public static void show(@StringRes int resId) {
        show(App.getContext().getString(resId));
    }

    public static void show(@StringRes int resId, Object... args) {
        show(App.getContext().getString(resId, args));
    }

    public static void show(final String msg) {
        App.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}