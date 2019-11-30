package ru.riselab.keitracker.adapters;

import android.content.Context;
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

import ru.riselab.keitracker.R;
import ru.riselab.keitracker.db.model.PointModel;

public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.PointViewHolder> {

    class PointViewHolder extends RecyclerView.ViewHolder {

        private final TextView pointItemTitleView;
        private final TextView pointItemTextView;

        private PointViewHolder(@NonNull View itemView) {
            super(itemView);

            pointItemTitleView = itemView.findViewById(R.id.pointItemTitle);
            pointItemTextView = itemView.findViewById(R.id.pointItemText);
        }
    }

    private final LayoutInflater mInflater;
    private List<PointModel> mPoints;

    public PointListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.point_item, parent, false);
        return new PointViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        if (mPoints != null) {
            PointModel current = mPoints.get(position);
            DateFormat dateFormat = new SimpleDateFormat(
                    "dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            String pointTextString = String.format(
                    "<i>latitude: <b>%s</b>, longitude: <b>%s</b>, altitude: <b>%s</b></i>",
                    current.getLatitude(),
                    current.getLongitude(),
                    current.getAltitude());
            holder.pointItemTitleView.setText(
                    String.format("%s) %s", position + 1, dateFormat.format(current.getFixedAt())));
            holder.pointItemTextView.setText(Html.fromHtml(pointTextString));
        } else {
            // TODO: process data not ready case
        }
    }

    public void setPoints(List<PointModel> points) {
        mPoints = points;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mPoints != null) {
            return mPoints.size();
        }
        return 0;
    }
}
