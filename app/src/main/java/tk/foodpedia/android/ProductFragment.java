package tk.foodpedia.android;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductFragment extends Fragment {
    private TextView textViewBarcode;

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_information, container, false);
        textViewBarcode = (TextView) v.findViewById(R.id.text_view_barcode);
        return v;
    }


    public void findProduct(String barcode) {
        textViewBarcode.setText(barcode);
    }
}
