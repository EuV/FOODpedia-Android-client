package tk.foodpedia.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.foodpedia.android.R;

public class SettingsFragment extends BaseFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_settings);
        return null;
    }
}
