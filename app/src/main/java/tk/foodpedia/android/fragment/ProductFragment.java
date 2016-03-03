package tk.foodpedia.android.fragment;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Loader;
import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.model.Product;

public class ProductFragment extends LoaderFragment implements OnRefreshListener {
    private static final String KEY_PRODUCT = "key_product";
    private static final String KEY_PRODUCT_ID = "key_product_id";

    private SwipeRefreshLayout refresher;

    private Product product;
    private String product_id;

    public static ProductFragment newInstance(@Nullable String barcode) {
        Bundle args = new Bundle();
        args.putString(KEY_PRODUCT_ID, barcode);
        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product_id = getArguments().getString(KEY_PRODUCT_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_information, container, false);
        refresher = (SwipeRefreshLayout) v.findViewById(R.id.product_refresher);
        refresher.setOnRefreshListener(this);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            product = (Product) savedInstanceState.get(KEY_PRODUCT);
            product_id = savedInstanceState.getString(KEY_PRODUCT_ID);
        }

        if (product == null) {
            loadProduct(false);
        } else {
            updateViews();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_PRODUCT, product);
        outState.putSerializable(KEY_PRODUCT_ID, product_id);
    }


    @SuppressWarnings("all")
    private void updateViews() {
        if (product != null) {
            product.fill((ViewGroup) getView().findViewById(R.id.product_views_container));
        }
    }


    private void loadProduct(boolean forced) {
        if (product_id == null) {
            animateLoading(false);
        } else {
            animateLoading(true);
            Loader.getInstance().load(this, Product.class, product_id, R.raw.query_product, forced);
        }
    }


    @Override
    protected void onLoadFinished(Downloadable downloadable) {
        animateLoading(false);
        if (downloadable == product) return;
        this.product = (Product) downloadable;
        updateViews();
    }


    @Override
    public void onLoadFailed() {
        animateLoading(false);
    }


    @Override
    public void onRefresh() {
        loadProduct(true);
    }


    /**
     * This WA is needed since SwipeRefreshLayout tends to miss
     * direct call of setRefreshing() in some cases.
     */
    private void animateLoading(final boolean refreshing) {
        refresher.post(new Runnable() {
            @Override
            public void run() {
                refresher.setRefreshing(refreshing);
            }
        });
    }
}
