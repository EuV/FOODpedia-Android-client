package tk.foodpedia.android;


import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class App extends Application {
    private static Thread uiThread;
    private static Context context;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        uiThread = Thread.currentThread();
        context = getApplicationContext();
        handler = new Handler(context.getMainLooper());
    }

    public static Context getContext() {
        return context;
    }

    public static void runOnUiThread(Runnable action) {
        if (Thread.currentThread() == uiThread) {
            action.run();
        } else {
            handler.post(action);
        }
    }
}
