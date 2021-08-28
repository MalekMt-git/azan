package ir.azan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ir.azan.utils.StrCities2List;
import static ir.azan.utils.StrCounries2List;
import static ir.azan.utils.StrOghat2List;
import static ir.azan.utils.sharedPrefGet;
import static ir.azan.utils.sharedPrefPut;


public class SearchCity extends AppCompatActivity {

    private ProgressBar progress_bar;
    private EditText et_search;
    private View lyt_content;
    private List<utils.Saved_Cities> list_saved_cities = new ArrayList<>();
    private int chosenCountryID=-1;
    private int chosenCityID=-1;
    //    private String [] countries = new String[]{"Netherlands"};
//    private String [] Cities_name = new String[]{"Amsterdam","Zoetermeer","Rotterdam","The Hague","Utrecht"};
    private String [] Cities_id ;//= new String[]{"686","26551","712","521","1130"};
    private final String more_cities =" Other cities ";
    private final String more_countries =" Other countries ";
    private final String more_cities_fa =" شهرهای بیشتر ";
    private final String more_countries_fa =" کشورهای بیشتر ";
    private final String choose_country_fa ="کشور خود را انتخاب کنید";
    private final String choose_city_fa = "شهر خود را انتخاب کنید";
    private String Lang;


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_search_outlet);

//        initToolbar();
        mContext = this;
        initComponent();
        initLang();
        RetriveSavedCountries_pref();
//        initCountries();
//        initCities();
    }
//
//    private void initToolbar() {
//        Tools.setSystemBarColor(this, android.R.color.white);
//        Tools.setSystemBarLight(this);
//    }

    private void initComponent() {
        lyt_content = findViewById(R.id.lyt_content);
        et_search = findViewById(R.id.et_search);
        progress_bar = findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

    }

    private void initLang(){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"lang");
        Lang = output;
        if (!Lang.equals("English")){
            ((TextView)findViewById(R.id.choose_country)).setText(choose_country_fa);
            ((TextView)findViewById(R.id.choose_city)).setText(choose_city_fa);
        }
    }

    private void highightCountryButton(Button btnTag, int finalI){
        if (chosenCountryID != -1) {
            Button btn = findViewById(chosenCountryID);
            btn.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.grey_100));
            btn.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.app_black));
        }
        btnTag.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.blue_400));
        btnTag.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.white));
        chosenCountryID = finalI;
//                    Toast.makeText(SearchCity.this, String.valueOf(finalI), Toast.LENGTH_SHORT).show();
        String country_iso2 = list_saved_cities.get(finalI).iso2;
        getCities(false, finalI);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initCountries(){
        FlexboxLayout layout = findViewById(R.id.countries);
        layout.removeAllViews();
        for (int i=0; i<list_saved_cities.size();i++){
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText(list_saved_cities.get(i).country);
            btnTag.setId(i);
            btnTag.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_blue));
            btnTag.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.app_black));
            int finalI = i;
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    highightCountryButton(btnTag, finalI);
//                    if (chosenCountryID != -1) {
//                        Button btn = findViewById(chosenCountryID);
//                        btn.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.grey_100));
//                        btn.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.app_black));
//                    }
//                    btnTag.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.blue_400));
//                    btnTag.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.white));
//                    chosenCountryID = finalI;
////                    Toast.makeText(SearchCity.this, String.valueOf(finalI), Toast.LENGTH_SHORT).show();
//                    String country_iso2 = list_saved_cities.get(finalI).iso2;
//                    getCities(false, finalI);

                }
            });
            layout.addView(btnTag);
        }
        Button btnTag = new Button(this);
        btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnTag.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_blue));
        btnTag.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.green_100));
        btnTag.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.app_black));
        btnTag.setText((Lang.equals("English"))?more_countries:more_countries_fa);
        btnTag.setId(list_saved_cities.size());
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCountries(true);
            }
        });
        layout.addView(btnTag);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initCities(int country_index){
        FlexboxLayout cities_layout = findViewById(R.id.cities);
        cities_layout.removeAllViews();

        for (int i = 0; i< list_saved_cities.get(country_index).cities_name.size(); i++){
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText(list_saved_cities.get(country_index).cities_name.get(i));
            btnTag.setId(i+1000);

            btnTag.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_blue));
            btnTag.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.app_black));
            int finalI = i;
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chosenCityID != -1) {
                        Button btn = findViewById(chosenCityID);
                        btn.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.grey_100));
                        btn.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.app_black));

                    }
                    btnTag.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.blue_400));
                    btnTag.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.white));

                    chosenCityID = finalI+1000;
                    getCityOghat(list_saved_cities.get(country_index).cities_ids.get(finalI),
                            list_saved_cities.get(country_index).cities_name.get(finalI));
//                    Toast.makeText(SearchCity.this, String.valueOf(finalI), Toast.LENGTH_SHORT).show();
                }
            });
            cities_layout.addView(btnTag);
        }
        Button btnTag = new Button(this);
        btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnTag.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_blue));
        btnTag.setTextColor(ContextCompat.getColorStateList(SearchCity.this, R.color.app_black));
        btnTag.setBackgroundTintList(ContextCompat.getColorStateList(SearchCity.this, R.color.green_100));
        btnTag.setText((Lang.equals("English"))?more_cities:more_cities_fa);
        btnTag.setId(list_saved_cities.get(country_index).cities_name.size());
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCities(true,country_index) ;
            }
        });
        cities_layout.addView(btnTag);
    }
    private void fullScreen(){
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //<< this
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }
    private void getCityOghat(String city_id, String city_name){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),city_id+":"+
                new SimpleDateFormat("yyyy").format(new Date()));
        if (!output.equals("NoKey") && !output.equals("NoValue")){
            Log.e("loadmore", "Offline Mode ...");
            setupNewCity(city_id,  city_name);
        }
        else {
            String isConnected = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"Internet");
            if (!isConnected.equals("NoKey") && !isConnected.equals("NoValue"))
                if (isConnected.equals("1")) {
                    Toast.makeText(mContext, "Please wait for a few seconds ...", Toast.LENGTH_LONG).show();
                    GetJsonApi okHttpHandler = new GetJsonApi(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(output);
                                Log.d("oghat", obj.toString());
                                boolean status = ((Boolean) obj.get("status")).booleanValue();
                                if (status) {
                                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), city_id + ":" +
                                            new SimpleDateFormat("yyyy").format(new Date()), output);
                                    setupNewCity(city_id, city_name);
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    okHttpHandler.execute("https://prayers-times.net/api/prayer_times?city_id=" + city_id + "&year=" +
                            new SimpleDateFormat("yyyy").format(new Date()), "GET");
                }else{
                    Intent myIntent = new Intent(mContext, NoInternetActivity.class);
                    mContext.startActivity(myIntent);
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"ActiveCity");
        if (!output.equals("NoKey") && !output.equals("NoValue")) {
            Intent myIntent = new Intent(mContext, PickDataActivity.class);
            mContext.startActivity(myIntent);
        }
    }

    private void setupNewCity(String city_id, String city_name){
        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "ActiveCity", city_name+":"+city_id);
        finish();
        Intent myIntent = new Intent(mContext, PickDataActivity.class);
        mContext.startActivity(myIntent);
    }
//    public void outletTypeClick(View view) {
//        if (view instanceof Button) {
//            Button b = (Button) view;
//            if (b.isSelected()) {
//                b.setTextColor(getResources().getColor(R.color.grey_40));
//            } else {
//                b.setTextColor(Color.WHITE);
//            }
//            b.setSelected(!b.isSelected());
//        }
//    }

    private void searchAction() {
        progress_bar.setVisibility(View.VISIBLE);
        lyt_content.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress_bar.setVisibility(View.GONE);
                lyt_content.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Search Submit", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private List<utils.Saved_Cities> saved_cities_str2list(String output){
        Type type = new TypeToken<List<utils.Saved_Cities>>() {
        }.getType();
        return (new Gson().fromJson(output,type));
    }

    private void RetriveSavedCountries_pref(){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"saved_cities");
        if (!output.equals("NoKey") && !output.equals("NoValue")){
            list_saved_cities = saved_cities_str2list(output);
            initCountries();
        }else{
            String output2 = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"countries");
            if (!output2.equals("NoKey") && !output2.equals("NoValue")){
                init_saved_countries(StrCounries2List(output2));
            }else
                getCountries(false);
        }
    }

    private void getCountries(boolean showDialog){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"countries");
        if (!output.equals("NoKey") && !output.equals("NoValue")){
            if (!showDialog)
                init_saved_countries(StrCounries2List(output));
            else
                showChooseCountryDialog(StrCounries2List(output));
        }else {
            String isConnected = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"Internet");
            if (!isConnected.equals("NoKey") && !isConnected.equals("NoValue"))
                if (isConnected.equals("1")) {
                    GetJsonApi okHttpHandler = new GetJsonApi(new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(output);
                                Log.d("countries", obj.toString());
                                boolean status = ((Boolean) obj.get("status")).booleanValue();
                                if (status) {
                                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "countries", output);
                                    if (!showDialog)
                                        init_saved_countries(StrCounries2List(output));
                                    else
                                        showChooseCountryDialog(StrCounries2List(output));
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    okHttpHandler.execute("https://prayers-times.net/api/countries", "GET");
                }else{
                    Intent myIntent = new Intent(mContext, NoInternetActivity.class);
                    mContext.startActivity(myIntent);
                }
        }
    }

    private void showChooseCountryDialog(List<utils.Countries> countriesList){
        List<String> countries= new ArrayList<>();
        if (countriesList.size()>0){
            for (int i=0;i<countriesList.size();i++)
                countries.add(countriesList.get(i).name);
            String[] titlesList_country = new String[countries.size()];
            titlesList_country = countries.toArray(titlesList_country);
            final android.app.AlertDialog.Builder dialog_country = new android.app.AlertDialog.Builder(mContext);
            dialog_country.setTitle("Choose your country:");
            final String[] finalTitlesList_country = titlesList_country;
            dialog_country.setItems(finalTitlesList_country, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: check the selected country is not already in the list
                    if (!SelectedCountryExist(countriesList.get(which).name)) {
                        utils.Saved_Cities saved_city = new utils.Saved_Cities();
                        saved_city.country = countriesList.get(which).name;
                        saved_city.iso2 = countriesList.get(which).iso2;
                        list_saved_cities.add(saved_city);
                        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "saved_cities", new Gson().toJson(list_saved_cities));
                        initCountries();
                        Button btn = findViewById(list_saved_cities.size()-1);
                        highightCountryButton(btn,list_saved_cities.size()-1);
                    }
                    else{
                        Log.e( "ChosenCountry: ", "selected country already existed in the list");
                        // selected country already existed in the list
                    }
                }
            });
            dialog_country.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
//                    fullScreen();
                }
            });
            dialog_country.create().show();
        }
    }

    private boolean SelectedCountryExist(String country){
        for (int i=0;i<list_saved_cities.size();i++){
            if (list_saved_cities.get(i).country.equals(country))
                return true;
        }
        return false;
    }

    private void init_save_cities(List<utils.Cities> cities, int country_index){

        if (!list_saved_cities.get(country_index).default_cities_loaded) {
            List<String> cities_name = new ArrayList<String>();
            List<String> cities_ids = new ArrayList<String>();
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).init_list) {
                    cities_name.add(cities.get(i).name);
                    cities_ids.add(cities.get(i).id);
                }
            }
            list_saved_cities.get(country_index).cities_name = cities_name;
            list_saved_cities.get(country_index).cities_ids = cities_ids;
            list_saved_cities.get(country_index).default_cities_loaded = true;
            sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "saved_cities", new Gson().toJson(list_saved_cities));
        }
        initCities(country_index);
    }
    private void init_saved_countries(List<utils.Countries> countries){
       for (int i=0; i<countries.size();i++){
           if (countries.get(i).init_list){
               utils.Saved_Cities saved_city = new utils.Saved_Cities();
               saved_city.country=countries.get(i).name;
               saved_city.iso2=countries.get(i).iso2;
               list_saved_cities.add(saved_city);
           }
       }
        initCountries();
        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "saved_cities", new Gson().toJson(list_saved_cities));
    }

    private void getCities(boolean showDialog, int country_index){
        String country_code = list_saved_cities.get(country_index).iso2;
        String country_name = list_saved_cities.get(country_index).country;

        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),country_code);
        if (!output.equals("NoKey") && !output.equals("NoValue")){
            if (showDialog) {
                showChooseCityDialog(StrCities2List(output), country_index);
            }else{
                init_save_cities(StrCities2List(output),country_index);
            }
            Log.e("loadmore", "Offline Mode ...");
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
                                Log.d("cities", obj.toString());
                                boolean status = ((Boolean) obj.get("status")).booleanValue();
                                if (status) {
                                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), country_code, output);
//                            Toast.makeText(SearchCity.this, country_name+"---> cities are downloaded", Toast.LENGTH_SHORT).show();
                                    if (showDialog) {
                                        showChooseCityDialog(StrCities2List(output), country_index);
                                    } else {
                                        init_save_cities(StrCities2List(output), country_index);
                                    }
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    okHttpHandler.execute("https://prayers-times.net/api/cities?country_iso2=" + country_code, "GET");
                }else{
                    Intent myIntent = new Intent(mContext, NoInternetActivity.class);
                    mContext.startActivity(myIntent);
                }
        }
    }
    private void showChooseCityDialog(List<utils.Cities> citiesList, int country_index){
        List<String> cities= new ArrayList<>();
        if (citiesList.size()>0){
            for (int i=0;i<citiesList.size();i++)
                cities.add(citiesList.get(i).name);
            String[] titlesList_city = new String[cities.size()];
            titlesList_city = cities.toArray(titlesList_city);
            final android.app.AlertDialog.Builder dialog_city = new android.app.AlertDialog.Builder(mContext);
            dialog_city.setTitle("Choose your city:");
            final String[] finalTitlesList_city = titlesList_city;
            dialog_city.setItems(finalTitlesList_city, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: check the selected city is not already in the list
                    String city_name = citiesList.get(which).name;

                    if (!CheckSelectedCityExist(country_index, city_name)) {
                        String city_id = citiesList.get(which).id;
                        list_saved_cities.get(country_index).cities_name.add(city_name);
                        list_saved_cities.get(country_index).cities_ids.add(city_id);
                        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "saved_cities", new Gson().toJson(list_saved_cities));
//                        Toast.makeText(mContext, String.valueOf(which), Toast.LENGTH_SHORT).show();
                        init_save_cities(citiesList, country_index);
                        getCityOghat(city_id, city_name);
                    }
                    else
                        Log.e( "CitySelection: ", "The selected city is already in the list");
                }
            });
            dialog_city.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
            dialog_city.create().show();
        }
    }

    private boolean CheckSelectedCityExist(int country_index, String city_name){
        List<String> cities = list_saved_cities.get(country_index).cities_name;
        for (int i=0; i<cities.size();i++)
            if (cities.get(i).equals(city_name))
                return true;

        return false;
    }
}
