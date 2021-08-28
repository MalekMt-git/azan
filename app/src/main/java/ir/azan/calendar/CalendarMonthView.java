package ir.azan.calendar;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import ir.azan.R;

import static ir.azan.utils.sharedPrefGet;


public class CalendarMonthView extends FrameLayout implements View.OnClickListener {

    private GridLayout mGridLayout;
    private ViewGroup mLayoutDays;
    private OnDayViewClickListener mListener;
    private CalendarDate mSelectedDate;
    private final String [] weekdays = new String[] {"شنبه","یکشنبه","دوشنبه","سه شنبه","چهارشنبه","پنجشنبه","جمعه"};
    private final String [] weekdays_label = new String[]{"saturday","sunday","monday","tuesday","wednesday","thursday","friday"};

    public CalendarMonthView(Context context) {
        super(context);
        init();
    }

    public void setOnDayViewClickListener(OnDayViewClickListener listener) {
        mListener = listener;
    }

    public void setSelectedDate(CalendarDate selectedDate) {
        mSelectedDate = selectedDate;
    }

    private void init() {
        inflate(getContext(), R.layout.view_calendar_month, this);
        mGridLayout = findViewById(R.id.view_calendar_month_grid);
        mLayoutDays = findViewById(R.id.view_calendar_month_layout_days);


    }

    public void changeLableslang2Fa(){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(getContext()),"lang");
        if (output.equals("فارسی")){
            for (int i=0;i<weekdays_label.length;i++){
                String textViewID = weekdays_label[i];
                int resID = getResources().getIdentifier(textViewID, "id", getContext().getPackageName());
                ((TextView)findViewById(resID)).setText(weekdays[i]);
                ((TextView)findViewById(resID)).setTextSize(12);

            }
        }

    }

    public void buildView(CalendarMonth calendarMonth) {
        buildDaysLayout();
        buildGridView(calendarMonth);
    }

    private void buildDaysLayout() {
        String[] days;
        days = getResources().getStringArray(R.array.days_sunday_array);

        for (int i = 0; i < mLayoutDays.getChildCount(); i++) {
            TextView textView = (TextView) mLayoutDays.getChildAt(i);
            textView.setText(days[i]);
        }
    }

    private void buildGridView(CalendarMonth calendarMonth) {
        int row = CalendarMonth.NUMBER_OF_WEEKS_IN_MONTH;
        int col = CalendarMonth.NUMBER_OF_DAYS_IN_WEEK;
        mGridLayout.setRowCount(row);
        mGridLayout.setColumnCount(col);

        int screenWidth = Utils.getScreenWidth(getContext());
        int width = screenWidth / col;

        for (CalendarDate date : calendarMonth.getDays()) {
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = width;
            params.height = LayoutParams.WRAP_CONTENT;

            CalendarDayView dayView = new CalendarDayView(getContext(), date);
            dayView.setContentDescription(date.toString());
            dayView.setLayoutParams(params);
            dayView.setOnClickListener(this);
            decorateDayView(dayView, date, calendarMonth.getMonth());
            mGridLayout.addView(dayView);
        }
    }

    private void decorateDayView(CalendarDayView dayView, CalendarDate day, int month) {
        if (day.getMonth() != month) {
            dayView.setOtherMothTextColor();
        } else {
            dayView.setThisMothTextColor();
        }

        if (mSelectedDate != null && mSelectedDate.isDateEqual(day)) {
            dayView.setPurpleSolidOvalBackground();
        } else {
            dayView.unsetPurpleSolidOvalBackground();
        }
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onDayViewClick((CalendarDayView) view);
        }
    }
}