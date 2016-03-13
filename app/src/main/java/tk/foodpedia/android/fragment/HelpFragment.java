package tk.foodpedia.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.foodpedia.android.R;

public class HelpFragment extends BaseFragment {

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_help);
        return null;
    }
}
