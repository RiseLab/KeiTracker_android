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
import java.util.List;
import java.util.Locale;

import ru.riselab.keitracker.MainActivity;
import ru.riselab.keitracker.R;
import ru.riselab.keitracker.TrackActivity;
import ru.riselab.keitracker.db.pojo.Track;

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
            Track current = mTracks.get(position);
            Intent intent = new Intent(context, TrackActivity.class);
            intent.putExtra(MainActivity.EXTRA_TRACK_UUID, current.trackUuid);
            context.startActivity(intent);
        }
    }

    private final LayoutInflater mInflater;
    private List<Track> mTracks;

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
            Track current = mTracks.get(position);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            String trackInfoString = String.format("<i>from <b>%s</b> to <b>%s</b></i>",
                    dateFormat.format(current.firstTime),
                    dateFormat.format(current.lastTime));
            holder.trackItemNameView.setText(String.format("%s) %s", position + 1, current.trackUuid));
            holder.trackItemInfoView.setText(Html.fromHtml(trackInfoString));
        } else {
            // TODO: process data not ready case
        }
    }

    public void setTracks(List<Track> tracks) {
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
