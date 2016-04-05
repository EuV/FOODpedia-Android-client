package tk.foodpedia.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import tk.foodpedia.android.fragment.popup.ProductCodeFragment;
import tk.foodpedia.android.view.CameraPreview;
import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Scanner;
import tk.foodpedia.android.concurrent.Scanner.ScannerCallbacks;

public class ScannerFragment extends BaseFragment {
    private static final String KEY_POP_UP_IS_SHOWN = "key_pop_up_is_shown";
    private static final int BARCODE_REQUEST_CODE = 0;

    private ScannerCallbacks scannerCallbacks;
    private Scanner scanner;
    private boolean popUpIsShown = false;

    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        scannerCallbacks = (ScannerCallbacks) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            popUpIsShown = savedInstanceState.getBoolean(KEY_POP_UP_IS_SHOWN);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_scanner);

        final View v = inflater.inflate(R.layout.fragment_scanner, container, false);

        Button manualInput = (Button) v.findViewById(R.id.button_manual_input);
        manualInput.getBackground().setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.MULTIPLY);
        manualInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                popUpIsShown = true;
                scanner.stopScan();

                ProductCodeFragment productCodeFragment = ProductCodeFragment.newInstance();
                productCodeFragment.setTargetFragment(ScannerFragment.this, BARCODE_REQUEST_CODE);
                productCodeFragment.show(getFragmentManager(), null);
            }
        });

        CameraPreview cameraPreview = ((CameraPreview) v.findViewById(R.id.camera_preview));
        scanner = Scanner.getInstance(scannerCallbacks, cameraPreview);

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != BARCODE_REQUEST_CODE) return;

        if (resultCode == Activity.RESULT_OK) {
            scannerCallbacks.onScanCompleted(data.getStringExtra(ProductCodeFragment.KEY_PRODUCT_CODE));
        } else {
            popUpIsShown = false;
            scanner.startScan();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!popUpIsShown) {
            scanner.startScan();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        scanner.stopScan();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_POP_UP_IS_SHOWN, popUpIsShown);
    }
}
