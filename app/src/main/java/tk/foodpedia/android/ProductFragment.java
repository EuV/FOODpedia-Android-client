package tk.foodpedia.android;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.model.Product;

public class ProductFragment extends Fragment implements LoaderManager.LoaderCallbacks<Downloadable> {
    private static final String KEY_PRODUCT = "key_product";

    private Product product;

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_information, container, false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_PRODUCT, product);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            product = (Product) savedInstanceState.get(KEY_PRODUCT);
        }
        updateViews();
        getLoaderManager().initLoader(0, null, this);
    }


    private void updateViews() {
        if (product != null) {
            product.fill((ViewGroup) getView());
        }
    }


    @Override
    public Loader<Downloadable> onCreateLoader(int id, Bundle bundle) {
        return new DataLoader(getContext(), bundle);
    }


    @Override
    public void onLoadFinished(Loader<Downloadable> loader, Downloadable product) {
        if (this.product == product) return;
        this.product = (Product) product;
        updateViews();
    }


    @Override
    public void onLoaderReset(Loader<Downloadable> loader) { /* */ }


    public void findProduct(String barcode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DataLoader.KEY_DOWNLOADABLE, new Product(barcode));
        getLoaderManager().restartLoader(0, bundle, this);
    }
}
