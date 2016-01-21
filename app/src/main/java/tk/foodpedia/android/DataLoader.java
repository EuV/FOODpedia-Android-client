package tk.foodpedia.android;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class DataLoader extends AsyncTaskLoader<String> {
    public static final String KEY_BARCODE = "key_barcode";

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private final String barcode;

    public DataLoader(Context context, @NonNull Bundle bundle) {
        super(context);
        barcode = bundle.getString(KEY_BARCODE, null);
    }


    @Override
    protected void onStartLoading() {
        if (barcode == null) return;
        forceLoad();
    }


    @Override
    public String loadInBackground() {
        if (!hasConnection()) {
            showWarning();
            return null;
        }

        String productData = null;
        try {
            productData = fetchProductData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productData;
    }


    protected boolean hasConnection() {
        NetworkInfo info = ((ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    protected String fetchProductData() throws IOException {
        InputStream is = null;

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://www.google.ru").openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();

            is = connection.getInputStream();
            return convertStreamToString(is);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }


    // TODO: Extract to helper
    private void showWarning() {
        new Handler(getContext().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), R.string.error_no_network_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
