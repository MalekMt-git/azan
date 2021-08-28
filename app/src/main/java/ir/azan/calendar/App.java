package ir.azan.calendar;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;

import ir.azan.R;


public class App extends Application {

    private static Context sContext;

    public static Context getAppContext() {
        return sContext;
    }
    public static boolean AzanPlayed= false;
    public static MediaPlayer player = new MediaPlayer();


    public static boolean isAzanPlayed() {
        return AzanPlayed;
    }

    public static void setAzanPlayed(boolean azanPlayed) {
        AzanPlayed = azanPlayed;
    }


    public static void play_azan_sound(){
        stopPlayer();
        final AssetFileDescriptor afd = sContext.getResources().openRawResourceFd(R.raw.azan);
        final FileDescriptor fileDescriptor = afd.getFileDescriptor();
        try {
            player.setDataSource(fileDescriptor, afd.getStartOffset(),
                    afd.getLength());
            player.setLooping(false);
            player.prepare();
            player.start();
        } catch (IOException ex) {
            Log.e( "play_pre_azan_sound: ", ex.getCause().getMessage());
        }
    }

    public static void stopPlayer(){
        try {
            if (player.isPlaying()) {
                player.stop();
                player.reset();
                player.release();
            }
        } catch (Exception e){
            Log.d( "stopPlayer: ", e.getCause().getMessage());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }
}
