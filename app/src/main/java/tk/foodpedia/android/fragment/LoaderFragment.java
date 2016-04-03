package tk.foodpedia.android.fragment;

import tk.foodpedia.android.App;
import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Loader.LoaderCallbacks;
import tk.foodpedia.android.concurrent.Loader.RootCause;
import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.util.ToastHelper;

public abstract class LoaderFragment extends BaseFragment implements LoaderCallbacks {

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
    public final void loadFailed(RootCause rootCause) {
        notifyUser(rootCause);
        if (isAdded()) {
            App.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onLoadFailed();
                }
            });
        }
    }

    // TODO: delegate to successors
    private void notifyUser(RootCause rootCause) {
        int messageId;

        switch (rootCause) {
            case NO_CONNECTION:
                messageId = R.string.error_no_network_connection;
                break;

            case EMPTY_RESULT:
                messageId = R.string.error_no_product_information;
                break;

            case PARSING_ERROR:
                messageId = R.string.error_failed_to_parse_data;
                break;

            case IO_ERROR:
            default:
                messageId = R.string.error_failed_to_load_data;
                break;
        }

        ToastHelper.show(messageId);
    }

    protected abstract void onLoadFinished(Downloadable downloadable);

    protected abstract void onLoadFailed();
}
