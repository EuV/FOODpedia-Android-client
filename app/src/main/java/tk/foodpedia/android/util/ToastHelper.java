package tk.foodpedia.android.util;

import android.support.annotation.StringRes;
import android.widget.Toast;

import tk.foodpedia.android.App;

public final class ToastHelper {
    private static final int MESSAGE_LENGTH_THRESHOLD = 65;
    private static final int PAUSE_BETWEEN_MESSAGES_MS = 4000;
    private static long lastMessageDisplayTime;

    private ToastHelper() { /* */ }

    /**
     * Displays message no more than once per predefined period of time.
     */
    public static void showOnce(@StringRes int resId, Object... args) {
        long currentTime = System.currentTimeMillis();
        if (currentTime > lastMessageDisplayTime + PAUSE_BETWEEN_MESSAGES_MS) {
            lastMessageDisplayTime = currentTime;
            show(resId, args);
        }
    }

    public static void show(@StringRes int resId, Object... args) {
        show(App.getContext().getString(resId, args));
    }

    public static void show(final String msg) {
        App.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = (msg.length() > MESSAGE_LENGTH_THRESHOLD) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
                Toast.makeText(App.getContext(), msg, duration).show();
            }
        });
    }
}
