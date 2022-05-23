package edu.ucsd.cse110.zoodata_demo;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ucsd.cse110.zoodata.ExhibitWithGroup;
import edu.ucsd.cse110.zoodata_demo.databinding.ExhibitListItemBinding;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private final List<ExhibitWithGroup> exhibitWithGroups = new ArrayList<>();

    public ExhibitListAdapter() {
        this.setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        ExhibitListItemBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var data = exhibitWithGroups.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.exhibit_list_item;
    }

    @Override
    public long getItemId(int position) {
        return exhibitWithGroups.get(position).exhibit.id.hashCode();
    }

    @Override
    public int getItemCount() {
        return exhibitWithGroups.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setExhibitWithGroups(List<ExhibitWithGroup> exhibitWithGroups) {
        this.exhibitWithGroups.clear();
        this.exhibitWithGroups.addAll(exhibitWithGroups);
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected final ExhibitListItemBinding binding;

        public ViewHolder(ExhibitListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExhibitWithGroup data) {
            binding.setVariable(BR.exhibitWithGroup, data);
            binding.executePendingBindings();
        }
    }
}
