package ir.azan;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class utils {
    private static final String[] faNumbers = new String[]{"۰","۱","۲","۳","۴","۵","۶","۷","۸","۹"};
    public static String convertNum_En2Fa(String text) {
        if (text.length() == 0) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out.append(faNumbers[number]);
            } else if (c == '٫' || c == ',') {
                out.append('،');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static final String[] monthName_en = new String[] {"January", "February", "March", "April", "May", "June",
            "July","August","September","October","November","December"};

    private static final String[] monthName_fa = new String[] {"ژانویه", "فوریه", "مارچ", "آپریل", "می", "جون",
            "جولای","آگوست","سپتامبر","اکتبر","نوامبر","دسامبر"};

    public static String monthName2fa(String weekday){

        for (int i=0;i<monthName_en.length;i++){
            if (weekday.equals(monthName_en[i]))
                return monthName_fa[i];
        }
        return "";
    }

    public static void sharedPrefRemove(SharedPreferences sharedPref, String key){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
        editor.commit();
    }
    public static void sharedPrefPut(SharedPreferences sharedPref, String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }
    public static String sharedPrefGet(SharedPreferences sharedPref, String key){
        if (sharedPref.contains(key))
            return  sharedPref.getString(key, "NoValue");
        else
            return "NoKey";
    }
    public static class Countries implements Serializable {

        public Boolean init_list;
        public String iso2;
        public String name;
    }

    public static List<Countries> StrCounries2List(String output){
        List<utils.Countries> countriesList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(output);
            JSONArray dataArray = obj.getJSONArray("data");
            for (int i=0; i<dataArray.length(); i++) {
                JSONObject data = dataArray.getJSONObject(i);
                utils.Countries country = new utils.Countries();
                country.iso2 = data.getString("iso2");
                country.name = data.getString("name");
                country.init_list = data.getInt("initial_list") == 1;
                countriesList.add(country);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return countriesList;
    }

    public static class Cities implements Serializable{
        public Boolean init_list;
        public String id;
        public String country_iso2;
        public String name;
        public String latitude;
        public String longitude;
    }

    public static List<Cities> StrCities2List(String output){
        List<utils.Cities> citiesList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(output);
            JSONArray dataArray = obj.getJSONArray("data");
            for (int i=0; i<dataArray.length(); i++) {
                JSONObject data = dataArray.getJSONObject(i);
                utils.Cities city = new utils.Cities();
                city.id = data.getString("id");
                city.country_iso2 = data.getString("country_iso2");
                city.name = data.getString("name");
                city.latitude = data.getString("latitude");
                city.longitude = data.getString("longitude");
                city.init_list = data.getInt("initial_list") == 1;
                citiesList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return citiesList;
    }

    public static class Saved_Cities implements Serializable{
        public String country;
        public String iso2;
        public List<String> cities_name = new ArrayList<String>();
        public List<String> cities_ids = new ArrayList<String>();
        public boolean default_cities_loaded = false;
    }

    public static class Oghat implements Serializable{
        public String date;
        public String imsak;
        public String fajr;
        public String sunrise;
        public String noon;
        public String asr;
        public String sunset;
        public String maghrib;
        public String isha;
        public String midnight;
    }

    public static List<Oghat> StrOghat2List(String output){
        List<utils.Oghat> oghatList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(output);
            JSONArray dataArray = obj.getJSONArray("data");
            for (int i=0; i<dataArray.length(); i++) {
                JSONObject data = dataArray.getJSONObject(i);
                utils.Oghat oghat = new utils.Oghat();
                oghat.date = data.getString("date");
                oghat.imsak = removeSecond(data.getString("imsak"), oghat.date);
                oghat.fajr = removeSecond(data.getString("fajr"), oghat.date);
                oghat.sunrise = removeSecond(data.getString("sunrise"), oghat.date);
                oghat.noon = removeSecond(data.getString("noon"), oghat.date);
                oghat.asr = removeSecond(data.getString("asr"), oghat.date);
                oghat.sunset = removeSecond(data.getString("sunset"), oghat.date);
                oghat.maghrib = removeSecond(data.getString("maghrib"), oghat.date);
                oghat.isha = removeSecond(data.getString("isha"), oghat.date);
                oghat.midnight = removeSecond(data.getString("midnight"), oghat.date);
                oghatList.add(oghat);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return oghatList;
    }

    private static String removeSecond(String input, String date){
        String result = "";
        try {
            String[] parts = input.split(":");
            result = parts[0] + ":" + parts[1];
        }
        catch (Exception e){
//            Log.e("removeSecond: ", date);
        }
        return result;
    }

    public static class NextOghat implements Serializable{
        public int index=-1;
        public long difference;
        public long previousOghatDifferece;
        public boolean oghatChanged=false;
    }

    public static List<utils.NextOghat> FindAllAzansLeft(String [] TodayOghat, String [] TomorrowOghat, int wakeup_time){
        List<utils.NextOghat> list_next_azans = new ArrayList<>();


        String [] currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()).split(":");
        long CurrentTimestamp = Long.valueOf(currentTime[0])*3600+Long.valueOf(currentTime[1])*60+Long.valueOf(currentTime[2]);

        long next_azan_timestamp = 0;
        String [] tempStr ;
        for (int i=0;i<TodayOghat.length;i++){
            utils.NextOghat next_oghat = new utils.NextOghat();
            next_oghat.index = 0;
            next_oghat.difference = 0;

            tempStr =TodayOghat[i].split(":");
            long temp = Long.valueOf(tempStr[0]);
            if (i==TodayOghat.length-1)
                if(temp<5)
                    temp +=24;
            next_azan_timestamp = temp*3600+Long.valueOf(tempStr[1])*60;
            if (CurrentTimestamp+wakeup_time<next_azan_timestamp && (i==1 || i==3 || i==5) ){
                next_oghat.index = i;
                next_oghat.difference = next_azan_timestamp - CurrentTimestamp;
                list_next_azans.add(next_oghat);
            }
        }

        for (int i=0;i<TomorrowOghat.length;i++) {
            utils.NextOghat next_oghat = new utils.NextOghat();
            next_oghat.index = 0;
            next_oghat.difference = 0;
            tempStr =TomorrowOghat[i].split(":");
            long temp = Long.valueOf(tempStr[0]) + 24;
            next_azan_timestamp = temp*3600+Long.valueOf(tempStr[1])*60;
            if (i==0 && (!TomorrowOghat[0].equals(TomorrowOghat[1]))){
                next_oghat.index = i;
                next_oghat.difference = next_azan_timestamp - CurrentTimestamp;
                list_next_azans.add(next_oghat);
            }
            if (i==1 || i==3 || i==5){
                next_oghat.index = i;
                next_oghat.difference = next_azan_timestamp - CurrentTimestamp;
                list_next_azans.add(next_oghat);
            }



        }
            return list_next_azans;
    }

    public static utils.NextOghat FindNextAzan(String [] Oghat, utils.NextOghat next_oghat){
//        next_oghat.index = 0;
//        next_oghat.difference = 0;
        String [] currentTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()).split(":");
        long currentTimeLong = Long.valueOf(currentTime[0])*3600+Long.valueOf(currentTime[1])*60+Long.valueOf(currentTime[2]);


        long next_azan_timestamp = 0;
        String [] tempStr ;
        tempStr =Oghat[Oghat.length-1].split(":");
        long previousAzanTimestamp=Long.valueOf(tempStr[0])*3600+Long.valueOf(tempStr[1])*60;

        for (int i=0;i<Oghat.length;i++){
            tempStr =Oghat[i].split(":");
            long temp = Long.valueOf(tempStr[0]);
            if (i==Oghat.length-1)
                if(temp<5)
                    temp +=24;
            next_azan_timestamp = temp*3600+Long.valueOf(tempStr[1])*60;
            if (currentTimeLong<next_azan_timestamp){
                if (next_oghat.index!=i && next_oghat.index!=-1) {
                    next_oghat.index = i;
                    next_oghat.oghatChanged=true;
                }else if(next_oghat.index==-1)
                    next_oghat.index = i;

                next_oghat.difference = next_azan_timestamp - currentTimeLong;
                next_oghat.previousOghatDifferece = currentTimeLong - previousAzanTimestamp;
                break;
            }
            previousAzanTimestamp = next_azan_timestamp;
        }
        return next_oghat;
    }

    public static String TimeDateDifferenceUntilNow(String d1_str, String current_str){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date d = null, current = null;
        try {
            d=dateFormat.parse(d1_str);
            current=dateFormat.parse(current_str);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = Math.abs(current.getTime() - d.getTime());
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        long elapsedSeconds = difference / secondsInMilli;
        String timeleft="";
        if (elapsedDays>0)
            if (elapsedDays>1)
                timeleft = elapsedDays + " Days";
            else
                timeleft = elapsedDays + " Day";

        else if (elapsedHours>0)
            if (elapsedHours>1)
                timeleft = elapsedHours + " Hours";
            else
                timeleft = elapsedHours + " Hour";
        else if (elapsedMinutes>0)
            if (elapsedMinutes>1)
                timeleft = elapsedMinutes + " Mins";
            else
                timeleft = elapsedMinutes + " Min";
        else if (elapsedSeconds > 0)
            if (elapsedSeconds>1)
                timeleft = elapsedSeconds + " Secs";
            else
                timeleft = elapsedSeconds + " Sec";

        return timeleft;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void isConnected(Context context) throws InterruptedException, IOException {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();
        Boolean vpn = false;
        Log.i("Network count: " , String.valueOf(networks.length));
        for(int i = 0; i < networks.length; i++) {

            NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);

            Log.i("Network ", i + ": " + networks[i].toString());
            Log.i("VPN transport is: ", String.valueOf(caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)));
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
                vpn= true;
            Log.i("NOT_VPN capability is: ", String.valueOf(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)));
        }
        final String command = "ping -c 1 one.com";
        if (vpn)
            sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(context),"Internet","1");
        else{
            int myInt = (Runtime.getRuntime().exec(command).waitFor() == 0) ? 1 : 0;
            sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(context),"Internet",String.valueOf(myInt));

        }
    }

    public static void CheckNewVersion(Context mContext, String AppVersion) {
        String isConnected = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext), "Internet");
        if (!isConnected.equals("NoKey") && !isConnected.equals("NoValue"))
            if (isConnected.equals("1")) {
                GetJsonApi okHttpHandler = new GetJsonApi(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(output);
                            boolean status = ((Boolean) obj.get("status")).booleanValue();
                            if (status) {
                                JSONObject obj2 = obj.getJSONArray("data").getJSONObject(0);
                                String version = obj2.getString("version");
                                if (Float.parseFloat(version) > Float.parseFloat(AppVersion)) {
                                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "update_link", obj2.getString("link"));
                                    if (obj2.getInt("force_update") == 1)
                                        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "update", "2");
                                    else
                                        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "update", "1");
                                }else {
                                    /*Currently the CheckNewVersion isn't done in a good practice as the update is defind with 0, 1 ,2 etc...*/
                                    sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "update", "0");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                okHttpHandler.execute("https://prayers-times.net/api/app_update", "GET");
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void CheckInternet(Context mContext){
        try {
            isConnected(mContext);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
