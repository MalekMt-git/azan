package ir.azan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.w3c.dom.Text;

import static ir.azan.utils.sharedPrefGet;

public class UpdateActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton update;
    private String updateLink;
    private Context context;
    private TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreen();
        setContentView(R.layout.activity_please_update);
        context = this;

        initComponent();
        initMessage();
        initUpdateButon();

    }

    private void initComponent(){
        update = findViewById(R.id.extended_fab);
        message = findViewById(R.id.message);
    }

    private void initUpdateButon(){
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLink = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(context),"update_link");
                if (!updateLink.equals("NoKey") && !updateLink.equals("NoValue")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateLink));
                    startActivity(browserIntent);
                }
            }
        });
    }
    private void initMessage() {
        String update = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(context), "update");
        if (!update.equals("NoKey") && !update.equals("NoValue")) {
            if (update.equals("1"))
                message.setText("Please update the app to enjoy new features!");
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