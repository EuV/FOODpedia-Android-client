package tk.foodpedia.android;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastHelper {
    private static Context context;
    private static Handler handler;

    private ToastHelper() { /* */ }

    public static void init(Activity activity) {
        context = activity.getApplicationContext();
        handler = new Handler(context.getMainLooper());
    }

    public static void show(@StringRes int resId) {
        show(context.getString(resId));
    }

    public static void show(@StringRes int resId, Object... args) {
        show(context.getString(resId, args));
    }

    public static void show(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void runOnUiThread(Runnable r) {
        handler.post(r);
    }
}
