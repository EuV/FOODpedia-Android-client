package tk.foodpedia.android;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.util.HashMap;
import java.util.Map;

import tk.foodpedia.android.fragment.HelpFragment;
import tk.foodpedia.android.fragment.HistoryFragment;
import tk.foodpedia.android.fragment.ProductFragment;
import tk.foodpedia.android.fragment.ScannerFragment;
import tk.foodpedia.android.concurrent.Scanner.ScannerCallbacks;
import tk.foodpedia.android.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity implements ScannerCallbacks, OnNavigationItemSelectedListener {
    private static final String KEY_FAB_IS_HIDDEN = "key_fab_is_hidden";
    private static final Map<Class, Integer> FRAGMENT_MENU_ID = new HashMap<>();

    static {
        FRAGMENT_MENU_ID.put(ProductFragment.class, R.id.nav_product);
        FRAGMENT_MENU_ID.put(ScannerFragment.class, R.id.nav_scanner);
        FRAGMENT_MENU_ID.put(HistoryFragment.class, R.id.nav_history);
        FRAGMENT_MENU_ID.put(SettingsFragment.class, R.id.nav_settings);
        FRAGMENT_MENU_ID.put(HelpFragment.class, R.id.nav_help);
    }

    private NavigationView drawerMenu;

    private boolean fabIsHidden = true;
    private boolean activityDestroyed = false;

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

        drawerMenu = (NavigationView) findViewById(R.id.drawer_menu);
        drawerMenu.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            placeFragment(ProductFragment.newInstance(null), true);
            placeFragment(ScannerFragment.newInstance(), false);
        } else {
            fabIsHidden = savedInstanceState.getBoolean(KEY_FAB_IS_HIDDEN);
        }

        if (fabIsHidden) findViewById(R.id.fab).setVisibility(View.GONE);

        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (fabIsHidden) return;
                placeFragment(ScannerFragment.newInstance(), false);
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FAB_IS_HIDDEN, fabIsHidden);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (removeTopFragment()) {
            return;
        }

        super.onBackPressed();
    }


    @Override
    public void onScanCompleted(String barcode) {
        if (activityDestroyed) return;
        placeFragment(ProductFragment.newInstance(barcode), true);
    }


    @Override
    protected void onDestroy() {
        activityDestroyed = true;
        super.onDestroy();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_product:
                placeFragment(ProductFragment.newInstance(null), true);
                break;
            case R.id.nav_scanner:
                placeFragment(ScannerFragment.newInstance(), false);
                break;
            case R.id.nav_history:
                placeFragment(HistoryFragment.newInstance(), true);
                break;
            case R.id.nav_settings:
                placeFragment(SettingsFragment.newInstance(), true);
                break;
            case R.id.nav_help:
                placeFragment(HelpFragment.newInstance(), true);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void placeFragment(Fragment newFragment, boolean clearBackStack) {
        if (newFragment == null) return;

        Fragment topFragment = getTopFragment();
        if (topFragment != null && topFragment.getClass() == newFragment.getClass()) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);

        if (clearBackStack) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        syncLayoutWithFragment(newFragment);
    }


    protected boolean removeTopFragment() {
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate();

        syncLayoutWithFragment(getTopFragment());

        return fragmentPopped;
    }


    protected void syncLayoutWithFragment(Fragment fragment) {
        if (fragment == null) return;

        if (fragment instanceof ScannerFragment) {
            hideFab();
        } else {
            showFab();
        }

        drawerMenu.setCheckedItem(FRAGMENT_MENU_ID.get(fragment.getClass()));
    }


    protected Fragment getTopFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }


    protected void hideFab() {
        if (fabIsHidden) return;
        Animation disappear = AnimationUtils.makeOutAnimation(this, true);
        disappear.setFillAfter(true);
        findViewById(R.id.fab).startAnimation(disappear);
        fabIsHidden = true;
    }


    protected void showFab() {
        if (!fabIsHidden) return;
        Animation appear = AnimationUtils.makeInAnimation(this, false);
        appear.setFillAfter(true);
        findViewById(R.id.fab).setVisibility(View.VISIBLE);
        findViewById(R.id.fab).startAnimation(appear);
        fabIsHidden = false;
    }
}
