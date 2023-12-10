package com.fitness.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.fitness.myapplication.ExerciseScheduler;
import com.fitness.myapplication.R;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<ExerciseScheduler.Result> dailyExerciseList;

    public ExerciseAdapter(List<ExerciseScheduler.Result> dailyExerciseList) {
        this.dailyExerciseList = dailyExerciseList;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_list_item, parent, false);

        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, int position) {
        ExerciseScheduler.Result result = dailyExerciseList.get(position);
        holder.exerciseName.setText(result.getExercise().getName());
        String weightTaken = result.getWeightTaken() + " kg";
        holder.weightTaken.setText(weightTaken);
    }

    @Override
    public int getItemCount() {
        return dailyExerciseList.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        public TextView exerciseName, weightTaken;

        public ExerciseViewHolder(View view) {
            super(view);
            exerciseName = view.findViewById(R.id.exercise_name);
            weightTaken = view.findViewById(R.id.weight_taken);
        }
    }
}

