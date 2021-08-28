package ir.azan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static ir.azan.utils.isConnected;
import static ir.azan.utils.sharedPrefGet;

public class NoInternetActivity extends AppCompatActivity {
    private AppCompatButton retry;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_no_item_internet_image);
        context=this;
        retry = findViewById(R.id.bt_retry);

        String Lang = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(context),"lang");
        if (!Lang.equals("English")){
            ((TextView)(findViewById(R.id.title))).setText("یک خبر ناگوار");
            ((TextView)(findViewById(R.id.message))).setText("شما به اینترنت متصل نیستید! لطفا دوباره امتحان کنید");
            ((AppCompatButton)(findViewById(R.id.bt_retry))).setText("تلاش مجدد");
        }

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    isConnected(context);
                    String isConnected = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(context),"Internet");
                    if (!isConnected.equals("NoKey") && !isConnected.equals("NoValue"))
                        if (isConnected.equals("1")){
                            showDialogLevel();
                        }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fullScreen(){
        getSupportActionBar().hide(); //<< this
    }


    private void showDialogLevel() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_achievement_level);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });
        String Lang = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(context),"lang");
        if (!Lang.equals("English")){
            ((TextView)(dialog.findViewById(R.id.title))).setText("یک خبر خوب!");
            ((TextView)(dialog.findViewById(R.id.message))).setText("شما دوباره به اینترنت متصل شدید");
            ((AppCompatButton)(dialog.findViewById(R.id.bt_ok))).setText("بسیار خوب");
        }
        dialog.show();
    }
}