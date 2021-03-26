package com.example.helloworld.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworld.R;

public class MyCalendarDialogManager extends Dialog {
    CalendarView calendarView;
    OnClick onClick;
    int i;
    public MyCalendarDialogManager(@NonNull Context context, OnClick onClick) {
        super(context,R.style.Theme_AppCompat_DayNight_Dialog);
        this.onClick = onClick;
    }

    public MyCalendarDialogManager(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyCalendarDialogManager(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_dialog);
        calendarView = this.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                onClick.onClickCalendar(year, month, dayOfMonth);
                dismiss();
            }
        });
    }

    public interface OnClick{
        public void onClickCalendar(int year, int month, int dayOfMonth);
    };

}
