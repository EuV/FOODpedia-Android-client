package tk.foodpedia.android;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProductInformationFragment extends Fragment {

    public static ProductInformationFragment newInstance() {
        return new ProductInformationFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_information, container, false);
    }
}
