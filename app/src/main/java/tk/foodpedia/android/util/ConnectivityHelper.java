package tk.foodpedia.android.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import tk.foodpedia.android.App;

import static android.content.Context.CONNECTIVITY_SERVICE;

public final class ConnectivityHelper {
    private static ConnectivityManager manager;

    private ConnectivityHelper() { /* */ }

    public static boolean hasConnection() {
        if (manager == null) {
            manager = (ConnectivityManager) App.getContext().getSystemService(CONNECTIVITY_SERVICE);
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }
}
