package fr.xgouchet.webmonitor.receiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import fr.xgouchet.webmonitor.common.Constants;


public class BootReceiver extends BroadcastReceiver {
    
    /** Android Log Tag */
    private static final String LOG_TAG = "BootReceiver";
    
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(LOG_TAG, "onReceive");
        Log.i(LOG_TAG, "Scheduling alarm from " + intent.getAction());
        
        scheduleServiceUpdates(context);
    }
    
    /**
     * Schedule the next update
     * 
     * @param context
     *            the current application context
     */
    private static void scheduleServiceUpdates(final Context context) {
        // create intent for our alarm receiver (or update it if it exists)
        final Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pending = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        // compute first call time n minutes after boot
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10); 
        long trigger = calendar.getTimeInMillis();
        
        // set delay between each call
        long delay = Constants.HOUR;
        
        // Set alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, trigger, delay, pending);
    }
}
