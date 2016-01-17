package tk.foodpedia.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import tk.foodpedia.android.ScannerFragment.OnScanCompletedListener;

import static android.R.anim.fade_in;
import static android.R.anim.fade_out;

public class MainActivity extends AppCompatActivity implements OnScanCompletedListener {
    private static final String KEY_FAB_IS_HIDDEN = "key_fab_is_hidden";
    private static final String KEY_PRODUCT_FRAGMENT = "key_product_fragment";

    private boolean fabIsHidden;
    private ProductFragment productFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState != null) {
            productFragment = (ProductFragment) fm.getFragment(savedInstanceState, KEY_PRODUCT_FRAGMENT);
            fabIsHidden = savedInstanceState.getBoolean(KEY_FAB_IS_HIDDEN);
        }

        if (productFragment == null) {
            productFragment = ProductFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_container, productFragment).commit();
        }

        if (fabIsHidden) findViewById(R.id.fab).setVisibility(View.GONE);

        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (fabIsHidden) return;
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(fade_in, fade_out, fade_in, fade_out)
                        .add(R.id.fragment_container, new ScannerFragment())
                        .addToBackStack(null)
                        .commit();
                hideFab();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FAB_IS_HIDDEN, fabIsHidden);
        getSupportFragmentManager().putFragment(outState, KEY_PRODUCT_FRAGMENT, productFragment);
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof ScannerFragment) {
            ((ScannerFragment) fragment).closeScanner();
            showFab();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onScanCompleted(String barcode) {
        productFragment.findProduct(barcode);
        showFab();
    }


    protected void hideFab() {
        Animation disappear = AnimationUtils.makeOutAnimation(this, true);
        disappear.setFillAfter(true);
        findViewById(R.id.fab).startAnimation(disappear);
        fabIsHidden = true;
    }


    protected void showFab() {
        Animation appear = AnimationUtils.makeInAnimation(this, false);
        appear.setFillAfter(true);
        findViewById(R.id.fab).setVisibility(View.VISIBLE);
        findViewById(R.id.fab).startAnimation(appear);
        fabIsHidden = false;
    }
}
