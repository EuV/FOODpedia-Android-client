package tk.foodpedia.android.concurrent;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RawRes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tk.foodpedia.android.App;
import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.model.Product;
import tk.foodpedia.android.model.meta.Additive;
import tk.foodpedia.android.model.meta.ServerResponse;
import tk.foodpedia.android.util.ConnectivityHelper;
import tk.foodpedia.android.util.StringHelper;

public final class Loader extends HandlerThread {
    private static final String ENDPOINT = "http://foodpedia.tk/sparql?query=";
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private static Loader loaderInstance;

    private Handler loaderThreadHandler;
    private Map<String, Downloadable> dataCache = new ConcurrentHashMap<>();

    public interface LoaderCallbacks {
        void loadFinished(Downloadable downloadable);
        void loadFailed(RootCause rootCause);
    }

    private Loader() {
        super("LoaderThread");
        start();
        loaderThreadHandler = new Handler(getLooper());
    }


    public static Loader getInstance() {
        if (loaderInstance == null) {
            loaderInstance = new Loader();
        }
        return loaderInstance;
    }


    public void load(final LoaderCallbacks destination,
                     final Class<? extends Downloadable> clazz,
                     final String downloadableId,
                     @RawRes final int queryResId,
                     boolean forced) {

        if (!forced) {
            if (dataCache.containsKey(downloadableId)) {
                destination.loadFinished(dataCache.get(downloadableId));
                return;
            }
        }

        loaderThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                loadInBackground(destination, clazz, downloadableId, queryResId);
            }
        });
    }


    private void loadInBackground(LoaderCallbacks destination, Class<? extends Downloadable> clazz, String downloadableId, @RawRes int queryResId) {
        if (!ConnectivityHelper.hasConnection()) {
            destination.loadFailed(RootCause.NO_CONNECTION);
            return;
        }

        String data;
        try {
            data = download(downloadableId, queryResId);
        } catch (IOException e) {
            destination.loadFailed(RootCause.IO_ERROR);
            return;
        }

        try {
            ServerResponse sr = JSON.parseObject(data, ServerResponse.class);
            Downloadable downloadable = JSON.parseObject(sr.getPayload(), clazz);

            // Workaround: Current version of SPARQL endpoint doesn't support GROUP_CONCAT,
            // so additives come as extra rows of query result
            if (downloadable instanceof Product) {
                Product product = (Product) downloadable;
                for (JSONObject additive : sr.getExtras()) {
                    product.addAdditive(JSON.parseObject(additive.toJSONString(), Additive.class).getValue());
                }
            }

            if (downloadable == null) {
                destination.loadFailed(RootCause.EMPTY_RESULT);
            } else {
                dataCache.put(downloadableId, downloadable); // TODO: implement persistent cache
                destination.loadFinished(downloadable);
            }
        } catch (JSONException e) {
            destination.loadFailed(RootCause.PARSING_ERROR);
        }
    }


    private String download(String downloadableId, @RawRes int queryResId) throws IOException {
        InputStream is = null;

        try {
            URLConnection connection = new URL(buildUrl(downloadableId, queryResId)).openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();

            is = connection.getInputStream();
            return StringHelper.fromStream(is);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    @SuppressWarnings("deprecation")
    private String buildUrl(String downloadableId, @RawRes int queryId) {
        String query = StringHelper.fromStream(App.getContext().getResources().openRawResource(queryId));
        return ENDPOINT + URLEncoder.encode(String.format(query, downloadableId));
    }


    public enum RootCause {
        NO_CONNECTION,
        IO_ERROR,
        EMPTY_RESULT,
        PARSING_ERROR
    }
}
