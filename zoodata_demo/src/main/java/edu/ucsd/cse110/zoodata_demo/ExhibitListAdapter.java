package edu.ucsd.cse110.zoodata_demo;

import android.annotation.SuppressLint;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import edu.ucsd.cse110.zoodata.ExhibitWithGroup;
import edu.ucsd.cse110.zoodata_demo.databinding.ExhibitListItemBinding;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private final List<ExhibitWithGroup> exhibitsWithGroups = new ArrayList<>();
    private Pair<Double, Double> lastKnownCoords = null;

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
        var data = exhibitsWithGroups.get(position);
        holder.bind(data, lastKnownCoords);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.exhibit_list_item;
    }

    @Override
    public long getItemId(int position) {
        return exhibitsWithGroups.get(position).exhibit.id.hashCode();
    }

    @Override
    public int getItemCount() {
        return exhibitsWithGroups.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setExhibitsWithGroups(List<ExhibitWithGroup> exhibitsWithGroups) {
        this.exhibitsWithGroups.clear();
        this.exhibitsWithGroups.addAll(exhibitsWithGroups);
        this.notifyDataSetChanged();
    }

    public void setLastKnownCoords(Pair<Double, Double> lastKnownCoords) {
        this.lastKnownCoords = lastKnownCoords;
        IntStream.range(0, exhibitsWithGroups.size())
            .forEach(position -> {
                var exhibitWithGroup = exhibitsWithGroups.get(position);
                if (exhibitWithGroup.isCloseTo(lastKnownCoords)) {
                    this.notifyItemChanged(position);
                }
            });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected final ExhibitListItemBinding binding;

        public ViewHolder(ExhibitListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExhibitWithGroup exhibitWithGroup, Pair<Double, Double> lastKnownCoords) {
            binding.setVariable(BR.exhibitWithGroup, exhibitWithGroup);
            binding.setVariable(BR.lastKnownCoords, lastKnownCoords);
            binding.executePendingBindings();
        }
    }
}
