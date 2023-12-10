package com.fitness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.fitness.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CheckableSpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> items;
    private boolean[] checkedItems;

    public CheckableSpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.mContext = context;
        this.items = items;
        this.checkedItems = new boolean[items.size()];
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.checkable_spinner_item, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        checkBox.setText(items.get(position));
        checkBox.setChecked(checkedItems[position]);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkedItems[position] = isChecked);

        return convertView;
    }

    public List<String> getCheckedItems() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (checkedItems[i]) {
                result.add(items.get(i));
            }
        }
        return result;
    }
}