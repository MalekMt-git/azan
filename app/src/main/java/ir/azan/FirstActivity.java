package ir.azan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static ir.azan.utils.isConnected;
import static ir.azan.utils.sharedPrefGet;
import static ir.azan.utils.sharedPrefPut;

public class FirstActivity extends AppCompatActivity {
    private View parent_view;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_first);
        mContext = this;
        parent_view = findViewById(android.R.id.content);

        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"lang");
        if (!output.equals("NoKey") && !output.equals("NoValue")) {
            openPickDateActivity();
//            showSingleChoiceDialog();
        }else
            showSingleChoiceDialog();


    }
    private static final String[] Languages = new String[]{
            "فارسی", "English"
    };
    private String single_choice_selected;
    private void showSingleChoiceDialog() {
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
                sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "lang", single_choice_selected);
                openPickDateActivity();
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    private void openPickDateActivity(){
        String output = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(mContext),"ActiveCity");
        sharedPrefPut(PreferenceManager.getDefaultSharedPreferences(mContext), "mode", "normal");

        if (!output.equals("NoKey") && !output.equals("NoValue")){
            finish();
            Intent myIntent = new Intent(mContext, PickDataActivity.class);
            mContext.startActivity(myIntent);
        } else{
            finish();
            Intent myIntent = new Intent(mContext, SearchCity.class);
            mContext.startActivity(myIntent);
        }
    }

    private void fullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide(); //<< this
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }
}