package ir.azan.calendar;

import android.content.Context;

import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.azan.R;

import static ir.azan.utils.convertNum_En2Fa;
import static ir.azan.utils.monthName2fa;
import static ir.azan.utils.sharedPrefGet;

/**
 * Created by ShirlyKadosh on 4/19/17.
 */

public class CustomCalendarView extends FrameLayout implements View.OnClickListener {

    private TextView mPagerTextMonth;
    private ImageButton mButtonLeftArrow;
    private ImageButton mButtonRightArrow;
    private ViewPager mViewPager;
    private CalendarViewPagerAdapter mViewPagerAdapter;
    private OnDateSelectedListener mListener;

    public CustomCalendarView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_custom_calendar, this);
        mViewPager = findViewById(R.id.activity_main_view_pager);
        mPagerTextMonth = findViewById(R.id.activity_main_pager_text_month);
        mButtonLeftArrow = findViewById(R.id.activity_main_pager_button_left_arrow);
        mButtonRightArrow = findViewById(R.id.activity_main_pager_button_right_arrow);
        mButtonLeftArrow.setOnClickListener(this);
        mButtonRightArrow.setOnClickListener(this);
        buildCalendarView();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mViewPagerAdapter.setOnDateSelectedListener(listener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_pager_button_right_arrow:
                int next = mViewPager.getCurrentItem() + 1;
                mViewPager.setCurrentItem(next, true);
                break;
            case R.id.activity_main_pager_button_left_arrow:
                int prev = mViewPager.getCurrentItem() - 1;
                mViewPager.setCurrentItem(prev, true);
                break;
        }
    }

    private void buildCalendarView() {
        List<CalendarMonth> list = new ArrayList<>();
        CalendarMonth today = new CalendarMonth(Calendar.getInstance());

        list.add(new CalendarMonth(today, -2));
        list.add(new CalendarMonth(today, -1));
        list.add(today);
        list.add(new CalendarMonth(today, 1));
        list.add(new CalendarMonth(today, 2));

        mViewPagerAdapter = new CalendarViewPagerAdapter(list, mViewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(2);
        String month_year = mViewPagerAdapter.getItemPageHeader(2);
        mPagerTextMonth.setText(CheckUserLang(month_year));

    }

    private String CheckUserLang(String month_year){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(getContext()),"lang");
        if (output.equals("فارسی")){
            String month = month_year.split(" ")[0];
            String year = month_year.split(" ")[2];
            return (monthName2fa(month)+"   "+convertNum_En2Fa(year));
        }
        return month_year;
    }

    private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int position = mViewPager.getCurrentItem();
            String month_year = mViewPagerAdapter.getItemPageHeader(position);
            mPagerTextMonth.setText(CheckUserLang(month_year));

            // current item is the first item in the list
            if (state == ViewPager.SCROLL_STATE_IDLE && position == 1) {
                addPrev();
            }

            // current item is the last item in the list
            if (state == ViewPager.SCROLL_STATE_IDLE && position == mViewPagerAdapter.getCount() - 2) {
                addNext();
            }

        }
    };

    private void addNext() {
        CalendarMonth month = mViewPagerAdapter.getItem(mViewPagerAdapter.getCount() - 1);
        mViewPagerAdapter.addNext(new CalendarMonth(month, 1));
    }

    private void addPrev() {
        CalendarMonth month = mViewPagerAdapter.getItem(0);
        mViewPagerAdapter.addPrev(new CalendarMonth(month, -1));
    }

}
