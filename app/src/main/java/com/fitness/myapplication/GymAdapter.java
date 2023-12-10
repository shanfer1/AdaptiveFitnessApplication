package com.fitness.myapplication;
import java.util.Locale;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.ViewHolder> {

    private List<Gym> gyms;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Gym gym);
    }

    public GymAdapter(List<Gym> gyms, OnItemClickListener listener) {
        this.gyms = gyms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gym, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gym gym = gyms.get(position);
        holder.bind(gym, listener);
        holder.textViewDistance.setText(String.format(Locale.getDefault(), "%.2f km away", gym.getDistance()));
    }

    @Override
    public int getItemCount() {
        return gyms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView gymNameTextView;
        TextView gymAddressTextView;
        TextView textViewDistance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gymNameTextView = itemView.findViewById(R.id.textViewName);
            gymAddressTextView = itemView.findViewById(R.id.textViewAddress);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
        }

        public void bind(final Gym gym, final OnItemClickListener listener) {
            itemView.setOnClickListener(view -> listener.onItemClick(gym));
            gymNameTextView.setText(gym.getName());
            gymAddressTextView.setText(gym.getAddress());
        }
    }
}