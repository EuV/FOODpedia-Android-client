package tk.foodpedia.android.fragment;

import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.Date;

import tk.foodpedia.android.App;
import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Database;
import tk.foodpedia.android.concurrent.Loader;
import tk.foodpedia.android.db.ProductRecord;
import tk.foodpedia.android.fragment.popup.ProductDescriptionFragment;
import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.model.Product;
import tk.foodpedia.android.util.EanHelper;
import tk.foodpedia.android.util.ToastHelper;

public class ProductFragment extends LoaderFragment implements OnRefreshListener {
    private static final int GRAPH_ANIMATION_DURATION = 500;
    private static final String KEY_FRAGMENT_STATE = "key_fragment_state";
    private static final String KEY_PRODUCT = "key_product";
    private static final String KEY_PRODUCT_ID = "key_product_id";

    private SwipeRefreshLayout refresher;

    private FragmentState fragmentState = FragmentState.INITIAL_LOADING;
    private Product product;
    private String productId;

    public static ProductFragment newInstance(@Nullable String barcode) {
        Bundle args = new Bundle();
        args.putString(KEY_PRODUCT_ID, checkBarcode(barcode) ? barcode : null);
        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private static boolean checkBarcode(@Nullable String barcode) {
        if (barcode == null) return false;

        if (!EanHelper.isValid(barcode)) {
            ToastHelper.show(R.string.error_product_code_invalid);
            return false;
        }

        if (EanHelper.isReserved(barcode)) {
            ToastHelper.show(R.string.error_product_code_for_internal_use);
            return false;
        }

        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            fragmentState = (FragmentState) savedInstanceState.get(KEY_FRAGMENT_STATE);
            product = (Product) savedInstanceState.get(KEY_PRODUCT);
            productId = savedInstanceState.getString(KEY_PRODUCT_ID);
            return;
        }

        productId = getArguments().getString(KEY_PRODUCT_ID);
        if (productId == null) {
            productId = App.getLastProductId();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_product);

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        initPieGraph((PieGraph) view.findViewById(R.id.pie_graph));

        view.findViewById(R.id.product_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product == null) return;
                ProductDescriptionFragment.newInstance(product).show(getFragmentManager(), null);
            }
        });

        refresher = (SwipeRefreshLayout) view.findViewById(R.id.product_container);
        refresher.setOnRefreshListener(this);
        refresher.setColorSchemeResources(R.color.graph_proteins, R.color.graph_carbohydrates, R.color.graph_fat);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (productId == null) {
            fragmentState = FragmentState.NO_INFORMATION;
        }

        updateFragmentState();

        switch (fragmentState) {
            case INITIAL_LOADING:
                loadProduct(false);
                break;

            case DISPLAY_INFORMATION:
                updateViews();
                saveHistory();
                break;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_FRAGMENT_STATE, fragmentState);
        outState.putSerializable(KEY_PRODUCT, product);
        outState.putString(KEY_PRODUCT_ID, productId);
    }


    @SuppressWarnings("deprecation")
    private void initPieGraph(PieGraph pieGraph) {
        Resources r = getResources();
        ArrayList<PieSlice> slices = new ArrayList<>();

        PieSlice proteins = new PieSlice();
        proteins.setColor(r.getColor(R.color.graph_proteins));
        slices.add(Slices.PROTEINS, proteins);

        PieSlice fat = new PieSlice();
        fat.setColor(r.getColor(R.color.graph_fat));
        slices.add(Slices.FAT, fat);

        PieSlice carbohydrates = new PieSlice();
        carbohydrates.setColor(r.getColor(R.color.graph_carbohydrates));
        slices.add(Slices.CARBOHYDRATES, carbohydrates);

        PieSlice neutral = new PieSlice();
        neutral.setColor(r.getColor(R.color.graph_neutral));
        neutral.setValue(100);
        slices.add(Slices.NEUTRAL, neutral);

        pieGraph.setSlices(slices);
    }


    @SuppressWarnings("all")
    private void updateViews() {
        if (product == null) return;
        product.fill((ViewGroup) getView().findViewById(R.id.product_container));

        PieGraph pg = (PieGraph) getView().findViewById(R.id.pie_graph);
        pg.getSlice(Slices.PROTEINS).setGoalValue(product.getProteinsSlice());
        pg.getSlice(Slices.FAT).setGoalValue(product.getFatSlice());
        pg.getSlice(Slices.CARBOHYDRATES).setGoalValue(product.getCarbohydratesSlice());
        pg.getSlice(Slices.NEUTRAL).setGoalValue(product.getNeutralSlice());
        pg.setInterpolator(new AccelerateDecelerateInterpolator());
        pg.setDuration(GRAPH_ANIMATION_DURATION);
        pg.animateToGoalValues();
    }


    private void saveHistory() {
        if (product == null) return;
        ProductRecord productRecord = new ProductRecord(product.getEan(), product.getName(), new Date());
        Database.getInstance().saveHistory(productRecord);
    }


    private void loadProduct(boolean forced) {
        if (fragmentState == FragmentState.DISPLAY_INFORMATION) {
            animateRefresher(true);
        }
        Loader.getInstance().load(this, Product.class, productId, R.raw.query_product, forced);
    }


    @Override
    protected void onLoadFinished(Downloadable downloadable) {
        if (fragmentState == FragmentState.DISPLAY_INFORMATION) {
            animateRefresher(false);
        } else {
            fragmentState = FragmentState.DISPLAY_INFORMATION;
            updateFragmentState();
        }

        if (downloadable == product) return;
        product = (Product) downloadable;

        updateViews();
        saveHistory();
        App.setLastProductId(productId);
    }


    @Override
    public void onLoadFailed() {
        switch (fragmentState) {

            case DISPLAY_INFORMATION:
                animateRefresher(false);
                break;

            case INITIAL_LOADING:
                fragmentState = FragmentState.NO_INFORMATION;
                updateFragmentState();
                break;
        }

        App.setLastProductId(null);
    }


    @Override
    public void onRefresh() {
        loadProduct(true);
    }


    /**
     * This WA is needed since SwipeRefreshLayout tends to miss
     * direct call of setRefreshing() in some cases.
     */
    private void animateRefresher(final boolean refreshing) {
        if (refresher == null) return;
        refresher.post(new Runnable() {
            @Override
            public void run() {
                refresher.setRefreshing(refreshing);
            }
        });
    }


    private void updateFragmentState() {
        setVisibility(R.id.product_progress_bar, fragmentState == FragmentState.INITIAL_LOADING);
        setVisibility(R.id.product_no_information, fragmentState == FragmentState.NO_INFORMATION);
        setVisibility(R.id.product_container, fragmentState == FragmentState.DISPLAY_INFORMATION);
    }


    private void setVisibility(@IdRes int viewId, boolean visible) {
        if (getView() == null) return;
        getView().findViewById(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    private enum FragmentState {
        INITIAL_LOADING,
        NO_INFORMATION,
        DISPLAY_INFORMATION
    }


    private static final class Slices {
        static final int PROTEINS = 0;
        static final int FAT = 1;
        static final int CARBOHYDRATES = 2;
        static final int NEUTRAL = 3;
    }
}
