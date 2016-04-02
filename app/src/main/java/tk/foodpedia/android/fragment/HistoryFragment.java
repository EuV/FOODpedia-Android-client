package tk.foodpedia.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Database;
import tk.foodpedia.android.concurrent.Database.DatabaseCallbacks;
import tk.foodpedia.android.db.ProductRecord;

public class HistoryFragment extends BaseFragment implements DatabaseCallbacks<ArrayList<ProductRecord>> {

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_history);
        Database.getInstance().loadHistory(this);
        return inflater.inflate(R.layout.fragment_history, container, false);
    }


    // TODO: rewrite
    @Override
    public void onReadDatabaseCompleted(ArrayList<ProductRecord> productRecords) {
        if (!isAdded()) return;
        for (ProductRecord productRecord : productRecords) {
            View record = getActivity().getLayoutInflater().inflate(R.layout.view_product_record, null);
            ((TextView) record.findViewById(R.id.product_record_name)).setText(productRecord.name);
            ((TextView) record.findViewById(R.id.product_record_last_viewed)).setText(productRecord.lastViewed.toString());
            ((LinearLayout) getView()).addView(record);
        }
    }
}
