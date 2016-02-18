package tk.foodpedia.android.fragment;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Loader;
import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.model.Product;

public class ProductFragment extends LoaderFragment {
    private static final String KEY_PRODUCT = "key_product";
    private static final String KEY_PRODUCT_ID = "key_product_id";

    private Product product;
    private String product_id;

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_information, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            product = (Product) savedInstanceState.get(KEY_PRODUCT);
            product_id = savedInstanceState.getString(KEY_PRODUCT_ID);
        }

        if (product == null) {
            loadProduct();
        } else {
            updateViews();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_PRODUCT, product);
        outState.putSerializable(KEY_PRODUCT_ID, product_id);
    }


    private void updateViews() {
        if (product != null) {
            product.fill((ViewGroup) getView());
        }
    }


    public void findProduct(String barcode) {
        product_id = barcode;
        loadProduct();
    }


    private void loadProduct() {
        if (product_id == null) return;
        Loader.getInstance().load(this, Product.class, product_id, R.raw.query_product, false);
    }


    @Override
    protected void onLoadFinished(Downloadable downloadable) {
        if (downloadable == product) return;
        this.product = (Product) downloadable;
        updateViews();
    }


    @Override
    public void onLoadFailed() {
        // ...
    }
}
