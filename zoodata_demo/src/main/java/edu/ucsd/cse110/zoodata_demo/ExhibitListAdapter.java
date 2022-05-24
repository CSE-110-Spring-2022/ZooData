package edu.ucsd.cse110.zoodata_demo;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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
        ExhibitListItemBinding binding = ExhibitListItemBinding.inflate(inflater, parent, false);
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

    @SuppressLint("NotifyDataSetChanged")
    public void setLastKnownCoords(Pair<Double, Double> lastKnownCoords) {
        Log.d("FOOBAR", String.format("Updating adapter lastKnownCoords to %s and notifying of changes...", lastKnownCoords));
        this.lastKnownCoords = lastKnownCoords;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected final ExhibitListItemBinding binding;

        public ViewHolder(ExhibitListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExhibitWithGroup exhibitWithGroup, Pair<Double, Double> lastKnownCoords) {
            binding.exhibitName.setText(exhibitWithGroup.getExhibitName());
            binding.exhibitLatlong.setText(exhibitWithGroup.getCoordString());
            binding.groupName.setText(exhibitWithGroup.getGroupName());

            var visibility = exhibitWithGroup.isCloseTo(lastKnownCoords) ? View.VISIBLE : View.GONE;
            binding.nearbyIndicator.setVisibility(visibility);

            if (visibility == View.VISIBLE) {
                Log.d("FOOBAR", String.format("Setting visibility of %s to VISIBLE!", exhibitWithGroup.exhibit.id));
            }
        }
    }
}
