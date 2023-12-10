package com.fitness.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fitness.myapplication.ExerciseScheduler;
import com.fitness.myapplication.R;

import java.util.List;

public class WeeklyExerciseAdapter extends RecyclerView.Adapter<WeeklyExerciseAdapter.DayViewHolder> {
    private List<List<ExerciseScheduler.Result>> weeklyExercises;

    public WeeklyExerciseAdapter(List<List<ExerciseScheduler.Result>> weeklyExercises) {
        this.weeklyExercises = weeklyExercises;
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        RecyclerView dailyRecyclerView;
        TextView dayHeader;

        public DayViewHolder(View itemView) {
            super(itemView);
            dailyRecyclerView = itemView.findViewById(R.id.daily_recycler_view);
            dailyRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            dayHeader = itemView.findViewById(R.id.day_header);
        }
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_layout_item, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        ExerciseAdapter dailyAdapter = new ExerciseAdapter(weeklyExercises.get(position));
        holder.dailyRecyclerView.setAdapter(dailyAdapter);

        String dayHeaderText = "Plan - Day " + (position + 1);
        holder.dayHeader.setText(dayHeaderText);
    }

    @Override
    public int getItemCount() {
        return weeklyExercises.size();
    }
}

