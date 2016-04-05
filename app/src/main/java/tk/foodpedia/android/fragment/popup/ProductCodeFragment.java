package tk.foodpedia.android.fragment.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import tk.foodpedia.android.R;
import tk.foodpedia.android.util.EanHelper;
import tk.foodpedia.android.util.StringHelper;

public class ProductCodeFragment extends DialogFragment {
    public static final String KEY_PRODUCT_CODE = "key_product_code";
    private EditText productCodeInput;

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
                        search(getInput());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancel();
                    }
                })
                .setView(setUpViews())
                .create();
    }


    private View setUpViews() {
        View root = getActivity().getLayoutInflater().inflate(R.layout.fragment_product_code, null);

        final TextInputLayout wrapper = (TextInputLayout) root.findViewById(R.id.product_code_input_wrapper);

        productCodeInput = (EditText) root.findViewById(R.id.product_code_input);
        productCodeInput.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EanHelper.isValid(getInput())) {
                    search(getInput());
                    dismiss();
                    return false;
                } else {
                    wrapper.setError(StringHelper.getString(R.string.label_product_code_tip));
                    return true;
                }
            }
        });

        return root;
    }


    private String getInput() {
        return productCodeInput.getText().toString();
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
