package ru.riselab.keitracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.riselab.keitracker.MainActivity;
import ru.riselab.keitracker.R;
import ru.riselab.keitracker.TrackActivity;
import ru.riselab.keitracker.db.model.TrackModel;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackViewHolder> {

    class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;
        private final TextView trackItemNameView;
        private final TextView trackItemInfoView;

        private TrackViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            trackItemNameView = itemView.findViewById(R.id.trackItemName);
            trackItemInfoView = itemView.findViewById(R.id.trackItemInfo);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            TrackModel current = mTracks.get(position);
            Intent intent = new Intent(context, TrackActivity.class);
            intent.putExtra(MainActivity.EXTRA_TRACK_ID, current.getId());
            context.startActivity(intent);
        }
    }

    private final LayoutInflater mInflater;
    private List<TrackModel> mTracks;

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

            String trackInfoString = String.format("<i>started: <b>%s</b>",
                    dateFormat.format(new Date(current.getStartedAt())));
            if (current.getStoppedAt() != null) {
                trackInfoString += String.format(", <i>stopped: <b>%s</b>",
                        dateFormat.format(new Date(current.getStoppedAt())));
            }

            holder.trackItemNameView.setText(String.format("%s) %s", position + 1, current.getName()));
            holder.trackItemInfoView.setText(Html.fromHtml(trackInfoString));
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
}
