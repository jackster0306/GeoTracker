package com.psyjg14.coursework2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.psyjg14.coursework2.databinding.TravelDataItemLayoutBinding;
import com.psyjg14.coursework2.model.TravelDataItem;

import java.util.ArrayList;
import java.util.List;

public class TravelDataItemAdapter extends RecyclerView.Adapter<TravelDataItemAdapter.TravelDataItemViewHolder>{
    private List<TravelDataItem> travelDataItemList;

    public TravelDataItemAdapter(List<TravelDataItem> travelDataItemList){
        if (travelDataItemList == null) {
            this.travelDataItemList = new ArrayList<>();
        } else {
            this.travelDataItemList = travelDataItemList;
        }
    }



    @NonNull
    @Override
    public TravelDataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TravelDataItemLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.travel_data_item_layout, parent, false);
        return new TravelDataItemViewHolder(binding);
    }

    public void setTravelDataItemList(List<TravelDataItem> travelDataItemList) {
        if (travelDataItemList == null) {
            this.travelDataItemList = new ArrayList<>();
        } else {
            this.travelDataItemList = travelDataItemList;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TravelDataItemViewHolder holder, int position) {
        TravelDataItem travelDataItem = travelDataItemList.get(position);
        holder.bind(travelDataItem);
    }

    @Override
    public int getItemCount() {
        return travelDataItemList.size();
    }

    static class TravelDataItemViewHolder extends RecyclerView.ViewHolder {
        private final TravelDataItemLayoutBinding binding;

        public TravelDataItemViewHolder(TravelDataItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TravelDataItem travelDataItem){
            binding.setTravelItem(travelDataItem);
            binding.executePendingBindings();
        }

    }
}
