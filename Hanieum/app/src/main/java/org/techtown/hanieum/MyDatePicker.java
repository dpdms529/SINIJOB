package org.techtown.hanieum;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDatePicker extends DialogFragment {

    private static final int MAX_YEAR = 2021;
    private static final int MIN_YEAR = 1960;

    private DatePickerDialog.OnDateSetListener listener;

    Button confirmBtn;
    Button cancelBtn;

    int startY;
    int startM;
    int endY;
    int endM;

    public MyDatePicker(int startY, int startM, int endY, int endM) {
        this.startY = startY;
        this.startM = startM;
        this.endY = endY;
        this.endM = endM;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.date_picker, null);

        confirmBtn = dialog.findViewById(R.id.confirmBtn);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);

        final NumberPicker monthPicker1 = (NumberPicker) dialog.findViewById(R.id.pickerMonth1);
        final NumberPicker yearPicker1 = (NumberPicker) dialog.findViewById(R.id.pickerYear1);
        final NumberPicker monthPicker2 = (NumberPicker) dialog.findViewById(R.id.pickerMonth2);
        final NumberPicker yearPicker2 = (NumberPicker) dialog.findViewById(R.id.pickerYear2);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatePicker.this.getDialog().cancel();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateSet(null, yearPicker1.getValue(), monthPicker1.getValue(), 0);
                listener.onDateSet(null, yearPicker2.getValue(), monthPicker2.getValue(), 0);
                MyDatePicker.this.getDialog().cancel();
            }
        });

        monthPicker1.setMinValue(1);
        monthPicker1.setMaxValue(12);
        monthPicker1.setValue(startM);

        yearPicker1.setMinValue(MIN_YEAR);
        yearPicker1.setMaxValue(MAX_YEAR);
        yearPicker1.setValue(startY);

        monthPicker2.setMinValue(1);
        monthPicker2.setMaxValue(12);
        monthPicker2.setValue(endM);

        yearPicker2.setMinValue(MIN_YEAR);
        yearPicker2.setMaxValue(MAX_YEAR);
        yearPicker2.setValue(endY);

        builder.setView(dialog);

        return builder.create();
    }
}
