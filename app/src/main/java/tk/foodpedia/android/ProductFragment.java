package tk.foodpedia.android;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {
    private TextView textViewBarcode;

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_information, container, false);
        textViewBarcode = (TextView) v.findViewById(R.id.text_view_barcode);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, new Bundle(), this);
    }


    @Override
    public Loader<String> onCreateLoader(int id, Bundle bundle) {
        return new DataLoader(getContext(), bundle);
    }

    // TODO: Handle redundant calling when screen rotation
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        textViewBarcode.setText(data);
    }


    @Override
    public void onLoaderReset(Loader<String> loader) { /* */ }


    public void findProduct(String barcode) {
        Bundle bundle = new Bundle();
        bundle.putString(DataLoader.KEY_BARCODE, barcode);
        getLoaderManager().restartLoader(0, bundle, this);
    }
}
