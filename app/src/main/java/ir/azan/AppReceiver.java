package ir.azan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import ir.azan.calendar.App;

import static ir.azan.utils.sharedPrefPut;

public class AppReceiver extends BroadcastReceiver {
    private static App PublicBoard;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PublicBoard = (App) context.getApplicationContext();
        String azanIndex="";
        if(intent.hasExtra("azanIndex"))
            azanIndex = intent.getStringExtra("azanIndex");

        boolean isScreenOn = pm.isInteractive();
        if (!isScreenOn) {
            sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(context), "mode", "notification");
            if (PickDataActivity.FULL_SCREEN_ACTION.equals(intent.getAction())) {
                PickDataActivity.CreateFullScreenNotification(context, azanIndex);

            }
        }else{

            Intent myIntent = new Intent(context, PickDataActivity.class);
            myIntent.putExtra("mode", "notification");
            myIntent.putExtra("azanIndex", azanIndex);
            context.startActivity(myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
