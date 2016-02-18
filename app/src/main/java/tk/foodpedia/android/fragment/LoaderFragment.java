package tk.foodpedia.android.fragment;

import android.support.v4.app.Fragment;

import tk.foodpedia.android.App;
import tk.foodpedia.android.concurrent.Loader.LoaderCallbacks;
import tk.foodpedia.android.model.Downloadable;

public abstract class LoaderFragment extends Fragment implements LoaderCallbacks {

    @Override
    public final void loadFinished(final Downloadable downloadable) {
        if (isAdded()) {
            App.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onLoadFinished(downloadable);
                }
            });
        }
    }

    @Override
    public final void loadFailed() {
        if (isAdded()) {
            App.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onLoadFailed();
                }
            });
        }
    }

    protected abstract void onLoadFinished(Downloadable downloadable);

    protected abstract void onLoadFailed();
}
