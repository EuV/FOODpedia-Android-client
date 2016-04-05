package tk.foodpedia.android.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import tk.foodpedia.android.view.CameraPreview;
import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Scanner;
import tk.foodpedia.android.concurrent.Scanner.ScannerCallbacks;

public class ScannerFragment extends BaseFragment {
    private ScannerCallbacks scannerCallbacks;
    private Scanner scanner;

    public static ScannerFragment newInstance() {
        return new ScannerFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        scannerCallbacks = (ScannerCallbacks) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_scanner);

        final View v = inflater.inflate(R.layout.fragment_scanner, container, false);

        Button manualInput = (Button) v.findViewById(R.id.button_manual_input);
        manualInput.getBackground().setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.MULTIPLY);

        // TODO: invoke dialog
        manualInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setOnClickListener(null);
                scannerCallbacks.onScanCompleted("4600605012994");
            }
        });

        CameraPreview cameraPreview = ((CameraPreview) v.findViewById(R.id.camera_preview));
        scanner = Scanner.getInstance(scannerCallbacks, cameraPreview);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        scanner.startScan();
    }


    @Override
    public void onPause() {
        super.onPause();
        scanner.stopScan();
    }
}
