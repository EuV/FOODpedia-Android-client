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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class DataLoader extends AsyncTaskLoader<String> {
    public static final String KEY_BARCODE = "key_barcode";

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private static final String CHARSET = "UTF-8";
    private static final String SPARQL_ENDPOINT = "http://foodpedia.tk/sparql";
    private static final String QUERY_PARAMETER = "?query=";
    private static final String SAMPLE_QUERY_BODY = "select ?name, ?mass, ?ingredients, ?energy\n" +
            "where {\n" +
            "    ?s <http://purl.org/goodrelations/v1#hasEAN_UCC-13> '%s'.\n" +
            "\n" +
            "    OPTIONAL { ?s <http://purl.org/goodrelations/v1#name> ?name. }\n" +
            "    OPTIONAL { ?s <http://foodpedia.tk/ontology#netto_mass> ?mass. }\n" +
            "    OPTIONAL { ?s <http://foodpedia.tk/ontology#esl> ?ingredients. }\n" +
            "    OPTIONAL { ?s <http://purl.org/foodontology#energyPer100gAsDouble> ?energy. }\n" +
            "\n" +
            "    #FILTER(langMatches(lang(?name), 'EN'))\n" +
            "    #FILTER(langMatches(lang(?mass), 'EN'))\n" +
            "    #FILTER(langMatches(lang(?ingredients), 'EN'))\n" +
            "}";

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
            HttpURLConnection connection = (HttpURLConnection) new URL(buildUrl()).openConnection();
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


    private String buildUrl() throws UnsupportedEncodingException {
        return SPARQL_ENDPOINT + QUERY_PARAMETER
                + URLEncoder.encode(String.format(SAMPLE_QUERY_BODY, barcode), CHARSET);
    }


    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is, CHARSET).useDelimiter("\\A");
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
