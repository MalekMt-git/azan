package ir.azan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ir.azan.calendar.App;
import ir.azan.calendar.CalendarDate;
import ir.azan.calendar.CustomCalendarView;
import ir.azan.calendar.OnDateSelectedListener;

import static ir.azan.utils.*;
import static ir.azan.utils.sharedPrefGet;

public class PickDataActivity extends AppCompatActivity implements OnDateSelectedListener {
    private TextView mTextDay;
    private TextView mTextDayOfWeek;
    private TextView mYear;
    private ScrollView scrollView;
    private TextView choose_date, choose_week_day,today_date,week_day,nextEvent;
    private CardView other_date_card;
    private CustomCalendarView mCustomCalendar;
    private ImageView settings;

    private final String[] monthName_en = new String[] {"January", "February", "March", "April", "May", "June",
            "July","August","September","October","November","December"};

    private final String[] monthName_fa = new String[] {"ژانوبه", "فوریه", "مارچ", "آپریل", "می", "جون",
            "جولای","آگوست","سپتامبر","اکتبر","نوامبر","دسامبر"};

    private final String [] weeks_fa = new String[]{"یکشنبه","دوشنبه","سه شنبه","چهارشنبه","پنجشنبه","جمعه","شنبه"};

    private final String [] NextEventStr_fa = new String[]{"تا امساک","تا اذان صبح","تا طلوع آفتاب","تا اذان ظهر"," تا غروب آفتاب",
            "تا اذان مغرب","تا نیمه شب",};
    private final String [] NextEventStr_en = new String[]{"Until Imsak","Until Fajr Prayer","Until Sunrise","Until Noon Prayer",
            "Until Sunset", "Until Maghrib Prayer","Until Midnight",};
    TextView emsak,sobh, tolu,zohr,ghorub,maghreb,nime_shab,time_left;
    TextView emsak2,sobh2, tolu2,zohr2,ghorub2,maghreb2,nime_shab2;

    private TextView [] all_oghat;

    private final int [] prayer_images = new int[]{R.drawable.fajr_400, R.drawable.fajr_400, R.drawable.sunrise_400,R.drawable.noon_400,
            R.drawable.sunset_400, R.drawable.maghrib_400, R.drawable.midnight_400};

    private final String [] panel_label_names = new String[]{"emsak_label","fajr_label","sunrise_label","zuhur_label","sunset_label","maghrib_label","midnight_label"};
    private final String [] panel_label_en = new String[]{"Imsak","Fajr","Sunrise","Noon","Sunset","Maghrib","Midnight"};

    private List<utils.Oghat> oghatList = new ArrayList<>();
    private List<utils.Oghat> oghatList_chooseDate = new ArrayList<>();
    private String current_year;
    private String current_city_id;
    private View parent_view;
    private boolean automatic_click= true;
    private String Lang;
    private final String AppVersion = "1.0";
    private Boolean pre_azan_scheduled = false;
    private Boolean NewActivity = false;
    private String mode;
    static NotificationManagerCompat notificationManager;
    private final int wakeup_time = 10; // the phone turns on 10 seconds before Azan
    private int counter = 0;
    private static App PublicBoard;
    private boolean settings_open = false;
    PowerManager.WakeLock screenLock;

    Context mContext;
    Button choose_city;

    private static final String CHANNEL_ID = "Azan_channel";
    static final String FULL_SCREEN_ACTION = "full_screen_action";
    static final int NOTIFICATION_ID = 1;
    utils.NextOghat next_oghat = new utils.NextOghat();
    PowerManager pm;
    String[] tomorrowOghatTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Intent intent=getIntent();
        if(intent.hasExtra("mode"))
            mode = intent.getStringExtra("mode");
        else
            mode = "normal";
        if (mode.equals("notification")) {
            // todo turn off the screen once azan is finished (after ~5 minutes)
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            turnOnScreen(this);
            FinishAppWithDelay();
        }
        fullScreen();


        setContentView(R.layout.activity_pick_data);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mContext = this;
        PublicBoard = (App) this.getApplicationContext();
        App.setAzanPlayed(false);


        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext),"Internet","0");
        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext),"Internet","0");

        CheckInternet(mContext);

        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"lang");
        if (output.equals("NoKey") || output.equals("NoValue")) {
            openSelectCityActivity();
        }

//        mode = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"mode");


        String azanIndex = "-1";
        if (mode.equals("notification")) {
            // todo turn off the screen once azan is finished (after ~5 minutes)
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            turnOnScreen(this);
//            FinishAppWithDelay();
            azanIndex = intent.getStringExtra("azanIndex");
            output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),getTodayData()+":"+azanIndex);
            if (output.equals("NoKey") || output.equals("NoValue")) {
                sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext),getTodayData()+":"+azanIndex,"0");
            }
        }

        initComponent();

        initSettings();

//        snackBarWithActionIndefinite();
        try {
            String isConnected = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"Internet");
            if (!isConnected.equals("NoKey") && !isConnected.equals("NoValue"))
                if (isConnected.equals("1"))
                    //CheckNewVersion(mContext, AppVersion);
                    /*Why is there AppVersion defined as final and no other assignment has been done to it? it is always
                     * as it's default value 1 */
                    CheckNewVersion(mContext, BuildConfig.VERSION_NAME);
        } catch (Exception e) {
            Log.e( "CheckUpdate: ", e.toString());
        }

        CheckUpdate();
        initLang();

        initChooseCity();
        ConvertNumbers2Farsi();

        loadActiveCityOghat();


        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String tomorrowStr = dateFormat.format(tomorrow);
        tomorrowOghatTime = upadateChooseDateCard(tomorrowStr);

        upadateChooseDateCard(today);
        updateTodayCard(today, getTodayData(), azanIndex);


    }


    @SuppressLint("InvalidWakeLockTag")
    private void turnOnScreen(Activity activity){

        screenLock =    ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Azan");
        screenLock.acquire(7*60*1000L /*7 minutes*/);

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // in addition to flags
            activity.setShowWhenLocked(true);
            activity.setTurnScreenOn(true);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                keyguardManager.requestDismissKeyguard(activity, new KeyguardManager.KeyguardDismissCallback() {
                    @Override
                    public void onDismissError() {
                        super.onDismissError();
                        Log.e("turnOnScreen", "Keyguard Dismiss Error");
                    }

                    @Override
                    public void onDismissSucceeded() {
                        super.onDismissSucceeded();
                        Log.e("turnOnScreen", "Keyguard Dismiss Success");
                    }

                    @Override
                    public void onDismissCancelled() {
                        super.onDismissCancelled();
                        Log.e("turnOnScreen", "Keyguard Dismiss Cancelled");
                    }
                });
            }
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
    }

    private void FinishAppWithDelay(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopPlayer();
                finish();
            }
        }, 7*60000);
    }

    private void openSelectCityActivity(){
        finish();
        NewActivity = true;
        Intent myIntent = new Intent(mContext, FirstActivity.class);
        mContext.startActivity(myIntent);
    }

    private void CheckUpdate(){
        String update = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"update");
        if (!update.equals("NoKey") && !update.equals("NoValue")) {
            NewActivity = true;
            if (update.equals("2")) {
                finish();
                Intent myIntent = new Intent(mContext, UpdateActivity.class);
                mContext.startActivity(myIntent);
            } else if (update.equals("1")){
                Intent myIntent = new Intent(mContext, UpdateActivity.class);
                mContext.startActivity(myIntent);
            }
        }
    }

    @Override
    public void onDateSelected(CalendarDate date) {

        Lang = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"lang");
        mTextDay.setText(date.dayToString());
        mTextDayOfWeek.setText(date.dayOfWeekToStringName());
        mYear.setText(date.yearToString());
        if (Lang.equals("English"))
            choose_date.setText(date.dayToString()+" "+ monthName_en[date.getMonth()]+" "+date.yearToString());
        else
            choose_date.setText(convertNum_En2Fa(date.dayToString())+" "+ monthName_fa[date.getMonth()]+" "+convertNum_En2Fa(date.yearToString()));

        Log.e("onDateSelected: ", String.valueOf(date.getMonth()));
        choose_week_day.setText((Lang.equals("English"))?date.dayOfWeekToStringName():weeks_fa[date.getDayOfWeek()-1]);
        if (!automatic_click) {
            other_date_card.setVisibility(View.VISIBLE);
            if (current_year.equals(date.yearToString()))
                upadateChooseDateCard(date.toString());
            else {
                getCityOghat(current_city_id, date.yearToString(), date.toString());
                emsak2.setText("-");
                sobh2.setText("-");
                tolu2.setText("-");
                zohr2.setText("-");
                ghorub2.setText("-");
                maghreb2.setText("-");
                nime_shab2.setText("-");
            }
        }
        else {
            automatic_click = false;
            week_day.setText((Lang.equals("English"))?date.dayOfWeekToStringName():weeks_fa[date.getDayOfWeek()-1]);
        }

    }
    private void initLang(){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"lang");
        if (!output.equals("NoKey") && !output.equals("NoValue")) {
            Lang = output;
            if (Lang.equals("English")){
                convertFa2EN();
            }
        }

    }

    private void convertFa2EN(){
        for (int i=0;i<panel_label_names.length;i++){
            String textViewID = panel_label_names[i];
            int resID = getResources().getIdentifier(textViewID, "id", getPackageName());
            ((TextView)findViewById(resID)).setText(panel_label_en[i]);

            resID = getResources().getIdentifier(textViewID+"_2", "id", getPackageName());
            ((TextView)findViewById(resID)).setText(panel_label_en[i]);
        }

        ((TextView)findViewById(R.id.prayer_times2)).setText("Prayer Times");
        ((TextView)findViewById(R.id.note)).setText("Note: All prayer times have been retrieved without any modification from Hamburg Islamic Center website (https://fa.izhamburg.com).");

        for (int i=0; i<all_oghat.length;i++)
            all_oghat[i].setTextSize(12);

    }
    private void nextAzan(String TodayDate, String azanIndex){
        counter = counter+1;
        String emsak_str = emsak.getText().toString();
        String sobh_str = sobh.getText().toString();
        String tolu_str = tolu.getText().toString();
        String zohr_str = zohr.getText().toString();
        String ghorub_str = ghorub.getText().toString();
        String maghreb_str = maghreb.getText().toString();
        String nime_shab_str = nime_shab.getText().toString();

        String[] oghatTime = new String[] {emsak_str, sobh_str, tolu_str, zohr_str, ghorub_str, maghreb_str,nime_shab_str};
        next_oghat = FindNextAzan(oghatTime, next_oghat);


        //اگر زمان امساک با زمان اذان صبح مشابه هست نیاز به نمایش امساک نیست
        int temp = -1;
        if (next_oghat.index==0 && emsak_str.trim().equals(sobh_str.trim()))
            temp=1;
        else
            temp = next_oghat.index;

        nextEvent.setText((Lang.equals("English"))?NextEventStr_en[temp]:NextEventStr_fa[temp]);
        time_left.setText((Lang.equals("English"))? difference2TimeRemained(next_oghat.difference)
                :convertNum_En2Fa(difference2TimeRemained(next_oghat.difference)));
        findViewById(R.id.prayer_image).setBackground(getResources().getDrawable(prayer_images[next_oghat.index]));
        List<utils.NextOghat> list_next_azans = FindAllAzansLeft(oghatTime, tomorrowOghatTime, wakeup_time);

        if (mode.equals("notification") && !pre_azan_scheduled && !App.isAzanPlayed()) {
            if (next_oghat.difference>wakeup_time && next_oghat.previousOghatDifferece<120) { // به این معناست که گوشی بعد از ساعت اذان روشن شده است
                // next_oghat.previousOghatDifferece<120 به این معناسب که اگر بیشتر از ۲ دقیقه از اذان گذشته و گوشی روشن شده دیگر اذان داده نشود
                String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),TodayDate+":"+azanIndex);
                if (output.equals("0")) { // to make sure this azanindex for today is not already played
                    App.setAzanPlayed(true);
                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), TodayDate + ":" + azanIndex, "1");

                    int current_oghat_index = next_oghat.index - 1;

                    switch (current_oghat_index){
                        case 1:
                            if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Fajr_selected")){
                                play_azan_sound();
                            }
                            break;
                        case 3:
                            if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Noon_selected")){
                                play_azan_sound();
                            }
                            break;
                        case 5:
                            if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Maghrib_selected")){
                                play_azan_sound();
                            }
                            break;
                    }
                }
                int current_oghat_index = next_oghat.index - 1;

                switch (current_oghat_index){
                    case 1:
                        if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Fajr_selected")){
                            App.setAzanPlayed(true); // to make sure the above if condition will not pass again
                        }
                        break;
                    case 3:
                        if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Noon_selected")){
                            App.setAzanPlayed(true); // to make sure the above if condition will not pass again
                        }
                        break;
                    case 5:
                        if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Maghrib_selected")){
                            App.setAzanPlayed(true); // to make sure the above if condition will not pass again
                        }
                        break;
                }

            }
            else
                list_next_azans.remove(0); // گوشی برای اذان روشن شده و هنوز چند ثانیه مانده، پس نیاز نیست دوباره آن اذان تنظیم شود

//            setPre_azan(list_next_azans);
//            pre_azan_scheduled = true;
        }
        if (mode.equals("notification"))
            NotificationManagerCompat.from(mContext).cancelAll();

//        else if (!pre_azan_scheduled && mode.equals("normal")) {
        setPre_azan(list_next_azans);
        pre_azan_scheduled = true;
//        }

        boolean isScreenOn = pm.isInteractive();
        if (next_oghat.oghatChanged && !App.isAzanPlayed() && isScreenOn){

            String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),TodayDate + ":" + azanIndex);
            if (output.equals("NoKey") || output.equals("NoValue") || output.equals("0")) {
                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), TodayDate + ":" + azanIndex, "1");
                    App.setAzanPlayed(true);
                    next_oghat.oghatChanged = false;

                int current_oghat_index = next_oghat.index - 1;

                switch (current_oghat_index){
                    case 1:
                        if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Fajr_selected") == true){
                            play_azan_sound();
                        }
                        break;
                    case 3:
                        if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Noon_selected") == true){
                            play_azan_sound();
                        }
                        break;
                    case 5:
                        if (sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Maghrib_selected") == true){
                            play_azan_sound();
                        }
                        break;
                }

            }else{
//                try {
//                    notificationManager.cancel(NOTIFICATION_ID);
//                } catch (Exception exception) {
//                    Log.e("play_azan_sound_e:", "00"+output+"00");
//                }
            }


        }

    }

    private String difference2TimeRemained(long diff){
        String result = "";
        if (diff >= 3600){
            long r = diff/3600;
            result = result + ((r<10)?"0":"")+ r +":";
            diff = diff - (r)*3600;
        }else
        result = result + "00:";
        if (diff>60){
            long r = diff/60;
            result = result + ((r<10)?"0":"")+ r +":";
            diff = diff - (r)*60;
        }else
            result = result + "00:";
        result = result + ((diff<10)?"0":"")+ diff;
        return result;
    }


    private void updateTodayCard(String date, String TodatDate, String azanIndex){
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      new Handler(Looper.getMainLooper()).post(new Runnable() {
                                          @Override
                                          public void run() {
                                              nextAzan(TodatDate, azanIndex);
                                              Log.d("UI thread", "I am the UI thread");
                                          }
                                      });
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                1000);

        String [] parts = date.split("/");
        String year = parts[2];
        String month =  parts[1];
        String day = parts[0];
        String TodayNewFormat = year+"-"+month+"-"+day;
        if (Lang.equals("English"))
            today_date.setText(day+" "+ monthName_en[Integer.valueOf(month)-1]+" "+year);
        else
            today_date.setText(convertNum_En2Fa(day)+" "+ monthName_fa[Integer.valueOf(month)-1]+" "+convertNum_En2Fa(year));

        String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
//        week_day.setText(weekday_name);

//        long str2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2021-03-13 15:40:45").getTime();
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String x = TimeDateDifferenceUntilNow(str, TodayNewFormat+" 15:40:45");


        if (oghatList.size()>0){
            for (int i=0; i<oghatList.size();i++){
                if (oghatList.get(i).date.equals(TodayNewFormat)){
                    emsak.setText((Lang.equals("English"))?oghatList.get(i).imsak:convertNum_En2Fa(oghatList.get(i).imsak));
                    sobh.setText((Lang.equals("English"))?oghatList.get(i).fajr:convertNum_En2Fa(oghatList.get(i).fajr));
                    tolu.setText((Lang.equals("English"))?oghatList.get(i).sunrise:convertNum_En2Fa(oghatList.get(i).sunrise));
                    zohr.setText((Lang.equals("English"))?oghatList.get(i).noon:convertNum_En2Fa(oghatList.get(i).noon));
                    ghorub.setText((Lang.equals("English"))?oghatList.get(i).sunset:convertNum_En2Fa(oghatList.get(i).sunset));
                    maghreb.setText((Lang.equals("English"))?oghatList.get(i).maghrib:convertNum_En2Fa(oghatList.get(i).maghrib));
                    nime_shab.setText((Lang.equals("English"))?oghatList.get(i).midnight:convertNum_En2Fa(oghatList.get(i).midnight));
                }
            }
        }
    }

    private String getTodayData(){
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = dateFormat.format(tomorrow);

        String [] parts = date.split("/");
        String year = parts[2];
        String month =  parts[1];
        String day = parts[0];
        String NewFormat = year+"-"+month+"-"+day;
        return NewFormat;
    }

    private static Integer getTodayDataStatic(){
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String date = dateFormat.format(tomorrow);

        String [] parts = date.split("/");
        String year = parts[2];
        String month =  parts[1];
        String day = parts[0];
        String NewFormat = year+month+day;
        return Integer.valueOf(NewFormat);
    }

    private  String[] upadateChooseDateCard(String date) {
        Log.e("New Date", date );
        String [] parts = date.split("/");
        String year = parts[2];
        String month =  parts[1];
        String day = parts[0];
        String NewFormat = year+"-"+month+"-"+day;

        if (oghatList_chooseDate.size()>0){
            for (int i=0; i<oghatList_chooseDate.size();i++){
                if (oghatList_chooseDate.get(i).date.equals(NewFormat)){
                    emsak2.setText((Lang.equals("English"))?oghatList.get(i).imsak:convertNum_En2Fa(oghatList.get(i).imsak));
                    sobh2.setText((Lang.equals("English"))?oghatList.get(i).fajr:convertNum_En2Fa(oghatList.get(i).fajr));
                    tolu2.setText((Lang.equals("English"))?oghatList.get(i).sunrise:convertNum_En2Fa(oghatList.get(i).sunrise));
                    zohr2.setText((Lang.equals("English"))?oghatList.get(i).noon:convertNum_En2Fa(oghatList.get(i).noon));
                    ghorub2.setText((Lang.equals("English"))?oghatList.get(i).sunset:convertNum_En2Fa(oghatList.get(i).sunset));
                    maghreb2.setText((Lang.equals("English"))?oghatList.get(i).maghrib:convertNum_En2Fa(oghatList.get(i).maghrib));
                    nime_shab2.setText((Lang.equals("English"))?oghatList.get(i).midnight:convertNum_En2Fa(oghatList.get(i).midnight));
                }
            }
        }
        String emsak_str = emsak2.getText().toString();
        String sobh_str = sobh2.getText().toString();
        String tolu_str = tolu2.getText().toString();
        String zohr_str = zohr2.getText().toString();
        String ghorub_str = ghorub2.getText().toString();
        String maghreb_str = maghreb2.getText().toString();
        String nime_shab_str = nime_shab2.getText().toString();
        String[] oghatTime = new String[] {emsak_str, sobh_str, tolu_str, zohr_str, ghorub_str, maghreb_str,nime_shab_str};

        return oghatTime;
    }

    private void initComponent(){

        settings = findViewById(R.id.settings);
        parent_view = findViewById(android.R.id.content);

        other_date_card = findViewById(R.id.other_date_card);

        scrollView = findViewById(R.id.scrollView);

        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"scrolled");
        if (output.equals("NoKey") && output.equals("NoValue")){
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            },2000);

            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            },4000);
            sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext),"scrolled","1");
        }


        nextEvent = findViewById(R.id.nextEvent);

        today_date = findViewById(R.id.today_date);
        week_day = findViewById(R.id.week_day);

        mTextDay = findViewById(R.id.activity_main_text_day_of_month);
        mTextDayOfWeek = findViewById(R.id.activity_main_text_day_of_week);
        mCustomCalendar = findViewById(R.id.activity_main_view_custom_calendar);
        mYear = findViewById(R.id.year);
        choose_date = findViewById(R.id.choose_date);
        choose_week_day = findViewById(R.id.choose_week_day);
        mCustomCalendar.setOnDateSelectedListener(this);
        choose_city = findViewById(R.id.choose_city);
        time_left = findViewById(R.id.time_left);

        emsak = findViewById(R.id.emsak);
        sobh = findViewById(R.id.sobh);
        tolu = findViewById(R.id.tolu);
        zohr = findViewById(R.id.zohr);
        ghorub = findViewById(R.id.ghorub);
        maghreb = findViewById(R.id.maghreb);
        nime_shab = findViewById(R.id.nime_shab);

        emsak2 = findViewById(R.id.emsak_2);
        sobh2 = findViewById(R.id.sobh_2);
        tolu2 = findViewById(R.id.tolu_2);
        zohr2 = findViewById(R.id.zohr_2);
        ghorub2 = findViewById(R.id.ghorub_2);
        maghreb2 = findViewById(R.id.maghreb_2);
        nime_shab2 = findViewById(R.id.nime_shab_2);


        all_oghat = new TextView[]{emsak,sobh, tolu,zohr,ghorub,maghreb,nime_shab,
                emsak2,sobh2, tolu2,zohr2,ghorub2,maghreb2,nime_shab2};
    }
    private void fullScreen(){
//    if (mode.equals("notification")){
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//    }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //<< this
//        getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }
    private void ConvertNumbers2Farsi(){
        TextView[] oghat = new TextView[] {emsak, sobh, tolu, zohr, ghorub, maghreb,nime_shab,time_left};
        TextView[] oghat2 = new TextView[] {emsak2, sobh2, tolu2, zohr2, ghorub2, maghreb2,nime_shab2};
        oghat[7].setText(convertNum_En2Fa(oghat[7].getText().toString()));
        for (int i=0;i<oghat2.length;i++) {
            oghat[i].setText(convertNum_En2Fa(oghat[i].getText().toString()));
            oghat2[i].setText(convertNum_En2Fa(oghat2[i].getText().toString()));

        }
    }
    private void initChooseCity(){
        choose_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getCountries();
                Intent myIntent = new Intent(PickDataActivity.this, SearchCity.class);
                PickDataActivity.this.startActivity(myIntent);
                NewActivity = true;
                finish();
            }
        });

    }

    private void getCityOghat(String city_id, String year, String chosen_date){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),current_city_id+":"+year);
        if (!output.equals("NoKey") && !output.equals("NoValue")){
            oghatList_chooseDate = StrOghat2List(output);
            upadateChooseDateCard(chosen_date);
            current_year = year;
        }
        else {
            String isConnected = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"Internet");
            if (!isConnected.equals("NoKey") && !isConnected.equals("NoValue"))
                if (isConnected.equals("1")) {
                    GetJsonApi okHttpHandler = new GetJsonApi(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(output);
                                Log.d("oghat", obj.toString());
                                boolean status = ((Boolean) obj.get("status")).booleanValue();
                                if (status) {
                                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), current_city_id + ":" + year, output);
                                    oghatList_chooseDate = StrOghat2List(output);
                                    upadateChooseDateCard(chosen_date);
                                    current_year = year;
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    okHttpHandler.execute("https://prayers-times.net/api/prayer_times?city_id=" + current_city_id + "&year=" + year, "GET");
                }
            else {
                    Intent myIntent = new Intent(mContext, NoInternetActivity.class);
                    mContext.startActivity(myIntent);
                    NewActivity = true;
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreen();
        NewActivity = false;
    }



private void loadActiveCityOghat(){
    String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"ActiveCity");
    if (!output.equals("NoKey") && !output.equals("NoValue")){
        String city_name = output.split(":")[0];
        String city_id = output.split(":")[1];
        String output2 = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),city_id+":"+
                new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date()));
        if (!output2.equals("NoKey") && !output2.equals("NoValue")){
            oghatList = StrOghat2List(output2);
            oghatList_chooseDate = oghatList;
            choose_city.setText(city_name);
            current_year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            current_city_id = city_id;
        }

    }
}
 private void initSettings(){
     settings.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             settings_open = true;
             showDialogFullscreen();
         }
     });
 }

    private void showDialogFullscreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogSettings newFragment = new DialogSettings();
//        newFragment.setRequestCode(DIALOG_QUEST_CODE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        newFragment.setOnCallbackResult(new DialogSettings.CallbackResult() {
            @Override
            public void sendResult(int requestCode) {
//                Toast.makeText(getApplicationContext(), "Menu clicked: "+requestCode, Toast.LENGTH_SHORT).show();
                switch(requestCode) {
                    case 1:
                        showSingleChoiceDialog();
                        break;
                    case 2:
                        showCustomDialog();
                        break;
                    case 3:
                        break;
                    case 4:
                        showOghatsChoiceDialog();
                }
            }

        });
    }

    private static final String[] Languages = new String[]{
            "فارسی", "English"
    };
    private String single_choice_selected;
    private void showSingleChoiceDialog() {
        final String Lang = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"lang");

        single_choice_selected = Languages[0];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Language");
        builder.setSingleChoiceItems(Languages, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                single_choice_selected = Languages[i];
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(parent_view, "selected : " + single_choice_selected, Snackbar.LENGTH_SHORT).show();
                if (!Lang.equals(single_choice_selected)) {
                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "lang", single_choice_selected);
                    settings_open = false;
                    ReLoad();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                settings_open=false;
            }
        });
        builder.show();
    }

    private void ReLoad(){
        finish();
        startActivity(getIntent());
        NewActivity = true;
    }


    private void showOghatsChoiceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String Lang = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(this),"lang");
        if (!Lang.equals("English")){
            builder.setTitle("لطفا اوقات مورد نظر خود را انتخاب کنید");
        }else{
            builder.setTitle("Choose which Oghats you want hear");
        }

        final String[] listItems = new String[]{"صبح/Fajr", "ظهر/Noon", "مغرب/Maghrib"};
        final boolean[] checkedItems = new boolean[listItems.length];

        checkedItems[0] = sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Fajr_selected");
        checkedItems[1] = sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Noon_selected");
        checkedItems[2] = sharedPrefGetOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Maghrib_selected");


        final List<String> selectedItems = Arrays.asList(listItems);

        builder.setMultiChoiceItems(listItems, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
            switch (which){
                case 0 : sharedPrefPutOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Fajr_selected", isChecked); break;
                case 1 : sharedPrefPutOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Noon_selected", isChecked); break;
                case 2 : sharedPrefPutOghats(PreferenceManager.getDefaultSharedPreferences(mContext), "is_Maghrib_selected", isChecked); break;
            }
        });


        builder.setPositiveButton("OK", (dialogInterface, i) -> {
        });
        builder.show();
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_review);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = dialog.findViewById(R.id.et_post);
        final AppCompatRatingBar rating_bar = dialog.findViewById(R.id.rating_bar);

        if (!Lang.equals("English")){
            ((EditText)(dialog.findViewById(R.id.name))).setHint("نام");
            ((EditText)(dialog.findViewById(R.id.tel))).setHint("شماره تلفن");
            et_post.setHint("لطفا گزارش خود را اینجا بنویسید ...");
            ((AppCompatButton)(dialog.findViewById(R.id.bt_cancel))).setText("لغو");
            ((AppCompatButton)(dialog.findViewById(R.id.bt_submit))).setText("ارسال");
        }

        dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings_open= false;
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bt_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = et_post.getText().toString().trim();
                if (review.equals("")) {
                    if (Lang.equals("English"))
                        Toast.makeText(getApplicationContext(), "Please fill review text", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "لطفا پیام خود را بنویسید", Toast.LENGTH_SHORT).show();
                }else {
                    submitRating(((EditText)(dialog.findViewById(R.id.name))).getText().toString(),((EditText)(dialog.findViewById(R.id.tel))).getText().toString(),
                            String.valueOf(((AppCompatRatingBar)dialog.findViewById(R.id.rating_bar)).getRating()), review);
                    dialog.dismiss();
                    settings_open=false;
                    if (Lang.equals("English"))
                        Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
                     else
                        Toast.makeText(getApplicationContext(), "ارسال شد", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void submitRating(String name, String tel, String score, String review){
        String msg = "name="+name+"-----tel="+tel+"-----comment="+review;
        String body = "rate="+score+"&message="+msg;

        GetJsonApi okHttpHandler = new GetJsonApi(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(output);
                    Log.d("oghat", obj.toString());
                    boolean status = ((Boolean) obj.get("status")).booleanValue();
                    if (status) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        okHttpHandler.execute("https://prayers-times.net/api/feedback?" + body, "POST");
    }

    private void play_azan_sound(){
//        NotificationManagerCompat.from(mContext).cancelAll();
//        notificationManager.cancel(NOTIFICATION_ID);
//
//        try {
//            notificationManager.cancel(NOTIFICATION_ID);
//        } catch (Exception exception) {
//            Log.e("play_azan_sound: ", "Notification cancelling error");
//        }
        App.play_azan_sound();
    }

    // Azan notification section
    public void setPre_azan(List<utils.NextOghat> list_next_azans) {
        for (int id = 0; id < list_next_azans.size(); id++) {
            Intent intent = new Intent(FULL_SCREEN_ACTION, null, this, AppReceiver.class);
            intent.putExtra("azanIndex", String.valueOf(list_next_azans.get(id).index));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            long TriggerTime = System.currentTimeMillis() +
                    (list_next_azans.get(id).difference-wakeup_time)*1000;

            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TriggerTime , pendingIntent);
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(TriggerTime, pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, TriggerTime , pendingIntent);
                }
                else alarmManager.set(AlarmManager.RTC_WAKEUP, TriggerTime , pendingIntent);
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
//                        (list_next_azans.get(id).difference-wakeup_time-5)*1000 , pendingIntent);
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
//                        (list_next_azans.get(id).difference-wakeup_time-10)*1000 , pendingIntent);

            }

//            NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID); //cancel last notification for repeated tests

//            AlarmManager mgr=
//                    (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
//            Intent i=new Intent(mContext, AppReceiver.class);
//            PendingIntent pi=PendingIntent.getBroadcast(mContext, id+313, i, PendingIntent.FLAG_UPDATE_CURRENT);
//            Intent i2=new Intent(mContext, PickDataActivity.class);
//            PendingIntent pi2=PendingIntent.getActivity(mContext, id+313, i2, 0);
//
//            AlarmManager.AlarmClockInfo ac=
//                    new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+list_next_azans.get(id).difference*1000 - 10,
//                            pi2);
//
//            mgr.setAlarmClock(ac, pi);
        }
    }

    public static void CreateFullScreenNotification(Context context, String azanIndex) {
        Integer Notificatio_ID = getTodayDataStatic()+Integer.valueOf(azanIndex);
        createNotificationChannel(context);

        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(context),"lang");
        String Lang = output, title,ContentText;
        if (!Lang.equals("English")){
            title = "برنامه اذان";
            ContentText = "وقت نماز است";
        }
        else {
            title = "Azan application";
            ContentText = "It's pray time";
        }

        Intent intent = new Intent(context, PickDataActivity.class);
        intent.putExtra("mode", "notification");
        intent.putExtra("azanIndex", azanIndex);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_SINGLE_TOP
                |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(ContentText)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
//                        .setContentIntent(pendingIntent)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setTimeoutAfter(2000)
                        .setFullScreenIntent(pendingIntent, true);
//        notificationBuilder.build().flags = notificationBuilder.build().FLAG_INSISTENT;
//        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder.build());
        NotificationManagerCompat.from(context).notify(Notificatio_ID, notificationBuilder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = NotificationManagerCompat.from(context);

            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel_name", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("channel_description");
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void stopPlayer(){
        App.stopPlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopPlayer();
        if (mode.equals("normal"))
            App.setAzanPlayed(false);
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus && !settings_open){
            if(!NewActivity) {
                finishAffinity();
                System.exit(0);
            }
            else
                finish();
        }
    }
}