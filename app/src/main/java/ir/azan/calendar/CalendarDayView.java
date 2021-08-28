package ir.azan.calendar;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import ir.azan.R;

import static ir.azan.utils.convertNum_En2Fa;
import static ir.azan.utils.sharedPrefGet;


public class CalendarDayView extends LinearLayout {

    private final CalendarDate mCalendarDate;
    private TextView mTextDay;
    private View mLayoutBackground;

    public CalendarDayView(Context context, CalendarDate calendarDate) {
        super(context);
        mCalendarDate = calendarDate;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_calendar_day, this);
        mLayoutBackground = findViewById(R.id.view_calendar_day_layout_background);
        mTextDay = findViewById(R.id.view_calendar_day_text);
        //Todo: change the number language in choose date form here
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(getContext()),"lang");
        mTextDay.setText("" + ((output.equals("English"))?mCalendarDate.getDay():convertNum_En2Fa(String.valueOf(mCalendarDate.getDay()))));
    }

    public CalendarDate getDate() {
        return mCalendarDate;
    }

    public void setThisMothTextColor() {
        mTextDay.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    public void setOtherMothTextColor() {
        mTextDay.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
    }

    public void setPurpleSolidOvalBackground() {
        mLayoutBackground.setBackgroundResource(R.drawable.oval_purple_solid);
    }

    public void unsetPurpleSolidOvalBackground() {
        mLayoutBackground.setBackgroundResource(R.drawable.oval_black_solid);
    }

}
