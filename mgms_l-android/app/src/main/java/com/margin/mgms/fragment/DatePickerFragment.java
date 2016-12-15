package com.margin.mgms.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.margin.mgms.misc.Config;
import com.margin.mgms.util.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created on Jul 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class DatePickerFragment extends DialogFragment {

    private static final DateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the config date as the default date in the picker
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(DateUtils.getDateFromString(sDateFormat,
                Config.DATE_STRING_TEST).getTime());
        // Create a new instance of DatePickerDialog and return it
        try {
            DatePickerDialog.OnDateSetListener listener =
                    (DatePickerDialog.OnDateSetListener) getActivity();
            return new DatePickerDialog(getActivity(), listener, c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement DatePickerDialog.OnDateSetListener!");
        }
    }
}
