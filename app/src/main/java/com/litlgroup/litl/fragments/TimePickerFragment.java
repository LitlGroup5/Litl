package com.litlgroup.litl.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 * Created by Hari on 8/27/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{


    public static TimePickerFragment newInstance(int minute, int hour, String amPm)
    {
        TimePickerFragment frag = new TimePickerFragment();
        Bundle args = new Bundle();
        int hour24 = amPm.equals("PM") ? hour + 12: hour;
        args.putInt("minute", minute);
        args.putInt("hour", hour24);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("minute");
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        try {

            TimePickerDialogListener listener = (TimePickerDialogListener) getActivity();

            String amPm = "AM";
            int hour = hourOfDay;
            if (hour > 12) {
                hour = hourOfDay - 12;
                amPm = "PM";
            }
            listener.onFinishTimePickDialog(hour, minute, amPm );
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public interface TimePickerDialogListener
    {
        void onFinishTimePickDialog(int hourOfDay, int minute, String amPm);
    }


}
