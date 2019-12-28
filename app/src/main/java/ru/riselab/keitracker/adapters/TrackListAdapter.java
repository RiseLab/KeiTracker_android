package ru.riselab.keitracker.adapters;


import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.riselab.keitracker.MainActivity;
import ru.riselab.keitracker.R;
import ru.riselab.keitracker.TrackActivity;
import ru.riselab.keitracker.db.model.TrackModel;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackViewHolder> {

    class TrackViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final Context context;
        private final TextView trackItemNameView;
        private final TextView trackItemInfoView;
        private final CheckBox trackItemCheckboxView;

        private TrackViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            trackItemNameView = itemView.findViewById(R.id.trackItemName);
            trackItemInfoView = itemView.findViewById(R.id.trackItemInfo);
            trackItemCheckboxView = itemView.findViewById(R.id.trackItemCheckbox);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mMassSelectMode) {
                toggleMassSelectMode(false);
                return;
            }

            int position = getLayoutPosition();
            TrackModel current = mTracks.get(position);
            Intent intent = new Intent(context, TrackActivity.class);
            intent.putExtra(MainActivity.EXTRA_TRACK_ID, current.getId());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            // TODO: check that track is not active
            if (!mMassSelectMode) {
                int position = getLayoutPosition();
                TrackModel current = mTracks.get(position);
                mSelectedTracks.clear();
                if (position != getItemCount() - 1 || !((MainActivity) context).isLocationServiceRunning()) {
                    mSelectedTracks.add(current.getId());
                }
                ((MainActivity) context)
                        .setItemsSelectedText(mSelectedTracks.size(), getItemCount());
                toggleMassSelectMode(true);
            }
            return true;
        }

        void toggleMassSelectMode(boolean enable) {
            mMassSelectMode = enable;
            ((MainActivity) context).toggleMenuEditMode(enable);
            notifyDataSetChanged();
        }
    }

    private final LayoutInflater mInflater;
    private List<TrackModel> mTracks;

    private boolean mMassSelectMode = false;
    private List<Integer> mSelectedTracks = new ArrayList<>();

    public TrackListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.track_item, parent, false);
        return new TrackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        if (mTracks != null) {
            // TODO: refactor
            TrackModel current = mTracks.get(position);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

            String trackInfoString = String.format("<i>%s: <b>%s</b>",
                    holder.context.getString(R.string.started), dateFormat.format(new Date(current.getStartedAt())));
            if (current.getStoppedAt() != null) {
                trackInfoString += String.format(", <i>%s: <b>%s</b>",
                        holder.context.getString(R.string.started), dateFormat.format(new Date(current.getStoppedAt())));
            }

            holder.trackItemNameView.setText(String.format("%s) %s", position + 1, current.getName()));
            holder.trackItemInfoView.setText(Html.fromHtml(trackInfoString));

            holder.trackItemCheckboxView.setEnabled(
                    position != getItemCount() - 1 || !((MainActivity) holder.context).isLocationServiceRunning());
            holder.trackItemCheckboxView.setChecked(mSelectedTracks.contains(current.getId()));
            if (mMassSelectMode) {
                holder.trackItemCheckboxView.setVisibility(View.VISIBLE);
                holder.trackItemCheckboxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (!mSelectedTracks.contains(current.getId())) {
                            mSelectedTracks.add(current.getId());
                        }
                    } else {
                        mSelectedTracks.removeAll(Collections.singletonList(current.getId()));
                    }
                    ((MainActivity) holder.context)
                            .setItemsSelectedText(mSelectedTracks.size(), getItemCount());
                });
            } else {
                holder.trackItemCheckboxView.setVisibility(View.INVISIBLE);
            }
        } else {
            // TODO: process data not ready case
        }
    }

    public void setTracks(List<TrackModel> tracks) {
        mTracks = tracks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mTracks != null) {
            return mTracks.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public List<Integer> getSelectedTracks() {
        mMassSelectMode = false;
        notifyDataSetChanged();
        return mSelectedTracks;
    }
}
