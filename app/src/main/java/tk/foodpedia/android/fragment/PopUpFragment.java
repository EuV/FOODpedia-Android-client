package tk.foodpedia.android.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import tk.foodpedia.android.model.Product;

public class PopUpFragment extends DialogFragment {
    private static final String KEY_PRODUCT = "key_product";
    private Product product;

    public static PopUpFragment newInstance(@NonNull Product product) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_PRODUCT, product);
        PopUpFragment fragment = new PopUpFragment();
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
                .setTitle(product.getEanFormatted())
                .setMessage(product.getDescriptionFormatted())
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
