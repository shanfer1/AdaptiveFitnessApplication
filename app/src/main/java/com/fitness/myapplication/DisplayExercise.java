package com.fitness.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import com.fitness.adapters.WeeklyExerciseAdapter;

import androidx.recyclerview.widget.RecyclerView;


public class DisplayExercise extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WeeklyExerciseAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_exercise);

        List<ExerciseScheduler.Result> flattenedList = getIntent().getParcelableArrayListExtra("FLATTENED_RESULT");
        ArrayList<Integer> groupSizes = getIntent().getIntegerArrayListExtra("GROUP_SIZES");

        List<List<ExerciseScheduler.Result>> reconstructedResult = reconstructNestedList(flattenedList, groupSizes);
        int i = 1;
        for(List<ExerciseScheduler.Result> resultList: reconstructedResult) {
            Log.i("DISPLAY", "DAY " + i++ + "  exercises");
            for (ExerciseScheduler.Result r : resultList) {
                Log.i("DISPLAY", r.exercise.name + " with weight " + r.weightTaken);
            }
        }
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WeeklyExerciseAdapter(reconstructedResult);
        recyclerView.setAdapter(adapter);
    }

    public static List<List<ExerciseScheduler.Result>> reconstructNestedList(
            List<ExerciseScheduler.Result> flattenedList, ArrayList<Integer> groupSizes) {

        List<List<ExerciseScheduler.Result>> nestedList = new ArrayList<>();
        int start = 0;

        for (int size : groupSizes) {
            List<ExerciseScheduler.Result> sublist = flattenedList.subList(start, start + size);
            nestedList.add(sublist);
            start += size;
        }

        return nestedList;
    }


}