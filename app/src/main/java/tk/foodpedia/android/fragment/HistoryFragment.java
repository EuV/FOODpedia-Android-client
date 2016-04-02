package tk.foodpedia.android.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import tk.foodpedia.android.R;
import tk.foodpedia.android.concurrent.Database;
import tk.foodpedia.android.concurrent.Database.DatabaseCallbacks;
import tk.foodpedia.android.db.ProductRecord;

public class HistoryFragment extends BaseFragment implements DatabaseCallbacks<ArrayList<ProductRecord>> {
    private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US);

    private RecyclerView historyRecyclerView;
    private ProgressBar historyProgressBar;
    private HistoryAdapter historyAdapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTitle(R.string.label_history);

        Database.getInstance().loadHistory(this);

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = (RecyclerView) rootView.findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerView.setAdapter(historyAdapter = new HistoryAdapter());

        historyProgressBar = (ProgressBar) rootView.findViewById(R.id.history_progress_bar);

        return rootView;
    }


    @Override
    public void onReadDatabaseCompleted(ArrayList<ProductRecord> productRecords) {
        if (!isAdded()) return;
        historyAdapter.setProductRecords(productRecords);
        historyRecyclerView.setVisibility(View.VISIBLE);
        historyProgressBar.setVisibility(View.GONE);
    }


    private class HistoryAdapter extends Adapter<ProductRecordViewHolder> {
        private ArrayList<ProductRecord> productRecords = new ArrayList<>();

        public void setProductRecords(ArrayList<ProductRecord> productRecords) {
            this.productRecords = productRecords;
            notifyDataSetChanged();
        }


        @Override
        public ProductRecordViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View record = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_product_record, parent, false);
            return new ProductRecordViewHolder(record);
        }


        @Override
        public void onBindViewHolder(ProductRecordViewHolder holder, int i) {
            ProductRecord productRecord = productRecords.get(i);
            holder.name.setText(productRecord.name);
            holder.lastViewed.setText(dateFormat.format(productRecord.lastViewed));
        }


        @Override
        public int getItemCount() {
            return productRecords.size();
        }
    }


    private class ProductRecordViewHolder extends ViewHolder {
        final TextView name;
        final TextView lastViewed;

        public ProductRecordViewHolder(View productRecordView) {
            super(productRecordView);
            name = (TextView) productRecordView.findViewById(R.id.product_record_name);
            lastViewed = (TextView) productRecordView.findViewById(R.id.product_record_last_viewed);
        }
    }
}
