package tk.foodpedia.android.fragment;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import tk.foodpedia.android.MainActivity;

public abstract class BaseFragment extends Fragment {

    /**
     * As long as there is the only Activity, don't care about interface
     * as 'best practice' here; just set the title directly.
     */
    @SuppressWarnings("all")
    protected void setTitle(@StringRes int titleResId) {
        MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(titleResId);
    }


    protected void placeFragment(Fragment fragment, boolean clearBackStack) {
        MainActivity activity = (MainActivity) getActivity();
        activity.placeFragment(fragment, clearBackStack);
    }
}
