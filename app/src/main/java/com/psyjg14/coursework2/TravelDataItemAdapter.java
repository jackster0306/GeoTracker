package com.psyjg14.coursework2;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.psyjg14.coursework2.databinding.TravelDataItemLayoutBinding;
import com.psyjg14.coursework2.model.TravelDataItem;

import java.util.List;

/**
 * Adapter for displaying a list of TravelDataItems in a RecyclerView using Data Binding.
 */
public class TravelDataItemAdapter extends RecyclerView.Adapter<TravelDataItemAdapter.TravelDataItemViewHolder> {

    /**
     * LiveData containing the list of TravelDataItems to be displayed.
     */
    private LiveData<List<TravelDataItem>> travelDataItemList;

    /**
     * Constructor for the TravelDataItemAdapter.
     *
     * @param travelDataItemList LiveData containing the list of TravelDataItems.
     * @param lifecycleOwner    LifecycleOwner for observing LiveData changes.
     */
    public TravelDataItemAdapter(LiveData<List<TravelDataItem>> travelDataItemList, LifecycleOwner lifecycleOwner) {
        this.travelDataItemList = travelDataItemList;

        // Observe changes in the LiveData and trigger notifyDataSetChanged when data changes.
        travelDataItemList.observe(lifecycleOwner, travelDataItems -> notifyDataSetChanged());
    }

    /**
     * Creates a new TravelDataItemViewHolder by inflating the layout from XML.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new TravelDataItemViewHolder that holds a TravelDataItemLayoutBinding.
     */
    @NonNull
    @Override
    public TravelDataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TravelDataItemLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.travel_data_item_layout, parent, false);
        return new TravelDataItemViewHolder(binding);
    }

    /**
     * Binds data to the TravelDataItemViewHolder.
     *
     * @param holder   The TravelDataItemViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull TravelDataItemViewHolder holder, int position) {
        List<TravelDataItem> travelDataItems = travelDataItemList.getValue();

        // Check if the list is not null and position is within bounds.
        if (travelDataItems != null && position < travelDataItems.size()) {
            TravelDataItem travelDataItem = travelDataItems.get(position);
            holder.bind(travelDataItem);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getItemCount() {
        List<TravelDataItem> travelDataItems = travelDataItemList.getValue();
        return travelDataItems != null ? travelDataItems.size() : 0;
    }

    /**
     * ViewHolder class for the TravelDataItemAdapter.
     * Holds a TravelDataItemLayoutBinding.
     */
    static class TravelDataItemViewHolder extends RecyclerView.ViewHolder {
        private final TravelDataItemLayoutBinding binding;

        /**
         * Constructor for TravelDataItemViewHolder.
         *
         * @param binding The binding associated with the layout.
         */
        public TravelDataItemViewHolder(TravelDataItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Binds a TravelDataItem to the layout.
         *
         * @param travelDataItem The TravelDataItem to bind.
         */
        void bind(TravelDataItem travelDataItem) {
            binding.setTravelItem(travelDataItem);
            binding.executePendingBindings();
        }
    }
}
