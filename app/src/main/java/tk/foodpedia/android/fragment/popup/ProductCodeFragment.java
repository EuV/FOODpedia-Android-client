package tk.foodpedia.android.fragment.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import tk.foodpedia.android.R;

public class ProductCodeFragment extends DialogFragment {
    public static final String KEY_PRODUCT_CODE = "key_product_code";

    public static ProductCodeFragment newInstance() {
        return new ProductCodeFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                .setPositiveButton(R.string.label_search, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText productCodeInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.product_code_input);
                        String productCode = productCodeInput.getText().toString();
                        search(productCode);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        cancel();
                    }
                })
                .setView(R.layout.fragment_product_code)
                .create();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        cancel();
    }


    private void search(String productCode) {
        Intent intent = new Intent();
        intent.putExtra(KEY_PRODUCT_CODE, productCode);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }


    // Don't call in onDismiss() since it is also invoked during fragment recreation
    private void cancel() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
    }
}
