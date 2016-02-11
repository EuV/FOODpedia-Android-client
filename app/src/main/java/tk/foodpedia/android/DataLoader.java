package tk.foodpedia.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.model.meta.ServerResponse;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class DataLoader extends AsyncTaskLoader<Downloadable> {
    public static final String KEY_DOWNLOADABLE = "key_downloadable";

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final String SPARQL_ENDPOINT = "http://foodpedia.tk/sparql";
    private static final String QUERY_PARAMETER = "?query=";

    private final Downloadable downloadable;

    public DataLoader(Context context, Bundle bundle) {
        super(context);
        downloadable = (bundle == null) ? null : (Downloadable) bundle.get(KEY_DOWNLOADABLE);
    }


    @Override
    protected void onStartLoading() {
        if (downloadable == null || downloadable.isDownloaded()) return;
        forceLoad();
    }


    @Override
    public Downloadable loadInBackground() {
        if (!hasConnection()) {
            ToastHelper.show(R.string.error_no_network_connection);
            return downloadable;
        }

        String data;
        try {
            data = download();
        } catch (IOException e) {
            ToastHelper.show(R.string.error_failed_to_load_data);
            return downloadable;
        }

        downloadable.downloaded();
        ServerResponse sr = JSON.parseObject(data, ServerResponse.class);
        return JSON.parseObject(sr.getPayload(), downloadable.getClass());
    }


    protected boolean hasConnection() {
        NetworkInfo info = ((ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    protected String download() throws IOException {
        InputStream is = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(buildUrl()).openConnection();
            connection.setRequestProperty("Accept", "application/json");
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


    @SuppressWarnings("deprecation")
    private String buildUrl() {
        String query = convertStreamToString(getContext().getResources().openRawResource(downloadable.getQueryId()));
        return SPARQL_ENDPOINT + QUERY_PARAMETER + URLEncoder.encode(String.format(query, downloadable.getQueryParams()));
    }


    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
