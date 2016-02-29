package tk.foodpedia.android;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import tk.foodpedia.android.fragment.ProductFragment;
import tk.foodpedia.android.fragment.ScannerFragment;
import tk.foodpedia.android.concurrent.Scanner.OnScanCompletedListener;

import static android.R.anim.fade_in;
import static android.R.anim.fade_out;

public class MainActivity extends AppCompatActivity implements OnScanCompletedListener, OnNavigationItemSelectedListener {
    private static final String KEY_FAB_IS_HIDDEN = "key_fab_is_hidden";
    private static final String KEY_PRODUCT_FRAGMENT = "key_product_fragment";
    private static final String KEY_SCANNER_FRAGMENT = "key_scanner_fragment";

    private boolean fabIsHidden;
    private ProductFragment productFragment;
    private ScannerFragment scannerFragment;

    // Substitutes API 17 isDestroyed()
    private boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_menu);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState != null) {
            productFragment = (ProductFragment) fm.getFragment(savedInstanceState, KEY_PRODUCT_FRAGMENT);
            scannerFragment = (ScannerFragment) fm.getFragment(savedInstanceState, KEY_SCANNER_FRAGMENT);
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
                        .replace(R.id.fragment_container, scannerFragment = new ScannerFragment())
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
        if (scannerFragment != null) {
            getSupportFragmentManager().putFragment(outState, KEY_SCANNER_FRAGMENT, scannerFragment);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (scannerFragment != null && scannerFragment.getUserVisibleHint()) {
            removeScannerFragment();
            showFab();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onScanCompleted(String barcode) {
        if (destroyed) return;
        productFragment.findProduct(barcode);
        removeScannerFragment();
        showFab();
    }


    @Override
    protected void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_product:
                // TODO
                break;
            case R.id.nav_scanner:
                // TODO
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    protected void removeScannerFragment() {
        if (scannerFragment == null) return;
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(fade_in, fade_out, fade_in, fade_out)
                .remove(scannerFragment)
                .commit();
        fm.popBackStack();
        scannerFragment = null;
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
