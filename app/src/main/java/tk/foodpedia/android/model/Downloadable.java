package tk.foodpedia.android.model;

import java.io.Serializable;

public abstract class Downloadable implements Serializable {
    private volatile boolean downloaded = false;

    public abstract int getQueryId();

    public abstract String[] getQueryParams();

    public void downloaded() {
        downloaded = true;
    }

    public boolean isDownloaded() {
        return downloaded;
    }
}
