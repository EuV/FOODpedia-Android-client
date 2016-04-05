package tk.foodpedia.android.fragment.popup;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import tk.foodpedia.android.model.Product;

public class ProductDescriptionFragment extends DialogFragment {
    private static final String KEY_PRODUCT = "key_product";
    private Product product;

    public static ProductDescriptionFragment newInstance(@NonNull Product product) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_PRODUCT, product);
        ProductDescriptionFragment fragment = new ProductDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = (Product) getArguments().get(KEY_PRODUCT);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setMessage(product.getDescriptionFormatted() + '\n' + product.getEanFormatted())
                .create();
    }
}
