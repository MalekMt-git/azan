package ir.azan;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import static ir.azan.utils.sharedPrefGet;


public class DialogSettings extends DialogFragment {

    public CallbackResult callbackResult;
    private Context context;

    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    private View root_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root_view = inflater.inflate(R.layout.dialog_settings, container, false);
        context = getActivity();

        String Lang = sharedPrefGet(PreferenceManager.getDefaultSharedPreferences(context),"lang");
        if (!Lang.equals("English")){
            ((TextView)(root_view.findViewById(R.id.settings))).setText("تنظیمات");

            ((TextView)(root_view.findViewById(R.id.lang_title))).setText("تغییر زبان");
            ((TextView)(root_view.findViewById(R.id.lang_message))).setText("زبان برنامه را تغییر دهید");

            ((TextView)(root_view.findViewById(R.id.feedback_title))).setText("گزارش خرابی");
            ((TextView)(root_view.findViewById(R.id.feedback_message))).setText("ارسال گزارش خرابی و یا پیشنهادات");

            ((TextView)(root_view.findViewById(R.id.contact_title))).setText("ارتباط با ما");
            ((TextView)(root_view.findViewById(R.id.contact_message))).setText("از طریق فضای مجازی با ما تماس بگیرید");

        }

        root_view.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        (root_view.findViewById(R.id.lyt_chang_lang)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPaymentClick(1);
            }
        });
        (root_view.findViewById(R.id.lyt_feedback)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPaymentClick(2);
            }
        });
        (root_view.findViewById(R.id.lyt_contact_us)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPaymentClick(3);
            }
        });
        return root_view;
    }

    private void onPaymentClick(int request_code) {
        if (callbackResult != null) {
            callbackResult.sendResult(request_code);
        }
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

//    public void setRequestCode(int request_code) {
//        this.request_code = request_code;
//    }

    public interface CallbackResult {
        void sendResult(int requestCode);
    }

}