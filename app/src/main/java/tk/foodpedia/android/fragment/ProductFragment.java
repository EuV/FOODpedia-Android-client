package tk.foodpedia.android.fragment;

import android.content.res.Resources;
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

import tk.foodpedia.android.App;
import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Loader;
import tk.foodpedia.android.model.Downloadable;
import tk.foodpedia.android.model.Product;

public class ProductFragment extends LoaderFragment implements OnRefreshListener {
    private static final int GRAPH_ANIMATION_DURATION = 500;
    private static final String KEY_PRODUCT = "key_product";
    private static final String KEY_PRODUCT_ID = "key_product_id";

    private SwipeRefreshLayout refresher;

    private Product product;
    private String productId;

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

        if (savedInstanceState != null) {
            product = (Product) savedInstanceState.get(KEY_PRODUCT);
            productId = savedInstanceState.getString(KEY_PRODUCT_ID);
            return;
        }

        productId = getArguments().getString(KEY_PRODUCT_ID);
        if (productId == null) {
            productId = App.getLastProductId();
        } else {
            App.setLastProductId(productId);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_product);

        if (productId == null) {
            return null;
        }

        View v = inflater.inflate(R.layout.fragment_product, container, false);
        initPieGraph((PieGraph) v.findViewById(R.id.pie_graph));
        refresher = (SwipeRefreshLayout) v.findViewById(R.id.product_refresher);
        refresher.setOnRefreshListener(this);
        refresher.setColorSchemeResources(R.color.graph_proteins, R.color.graph_carbohydrates, R.color.graph_fat);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (product == null) {
            loadProduct(false);
        } else {
            updateViews();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_PRODUCT, product);
        outState.putSerializable(KEY_PRODUCT_ID, productId);
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
        product.fill((ViewGroup) getView().findViewById(R.id.product_views_container));

        PieGraph pg = (PieGraph) getView().findViewById(R.id.pie_graph);
        pg.getSlice(Slices.PROTEINS).setGoalValue(product.getProteinsSlice());
        pg.getSlice(Slices.FAT).setGoalValue(product.getFatSlice());
        pg.getSlice(Slices.CARBOHYDRATES).setGoalValue(product.getCarbohydratesSlice());
        pg.getSlice(Slices.NEUTRAL).setGoalValue(product.getNeutralSlice());
        pg.setInterpolator(new AccelerateDecelerateInterpolator());
        pg.setDuration(GRAPH_ANIMATION_DURATION);
        pg.animateToGoalValues();
    }


    private void loadProduct(boolean forced) {
        if (productId == null) {
            animateLoading(false);
        } else {
            animateLoading(true);
            Loader.getInstance().load(this, Product.class, productId, R.raw.query_product, forced);
        }
    }


    @Override
    protected void onLoadFinished(Downloadable downloadable) {
        animateLoading(false);
        if (downloadable == product) return;
        product = (Product) downloadable;
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
        if (refresher == null) return;
        refresher.post(new Runnable() {
            @Override
            public void run() {
                refresher.setRefreshing(refreshing);
            }
        });
    }


    private static final class Slices {
        static final int PROTEINS = 0;
        static final int FAT = 1;
        static final int CARBOHYDRATES = 2;
        static final int NEUTRAL = 3;
    }
}
