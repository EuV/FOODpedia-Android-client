package tk.foodpedia.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class ScannerFragment extends Fragment {
    private OnScanCompletedListener onScanCompletedListener;

    public interface OnScanCompletedListener {
        void onScanCompleted(String barcode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onScanCompletedListener = (OnScanCompletedListener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_scanner, container, false);
        v.findViewById(R.id.button_find_product).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setOnClickListener(null);
                String barcode = ((EditText) v.findViewById(R.id.edit_text_barcode)).getText().toString();
                onScanCompletedListener.onScanCompleted(barcode);
            }
        });
        return v;
    }
}
