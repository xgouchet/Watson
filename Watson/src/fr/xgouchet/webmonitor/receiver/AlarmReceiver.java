package fr.xgouchet.webmonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import fr.xgouchet.webmonitor.service.UpdateService;


/**
 * Receiver called by a scheduled alarm
 * 
 * @author xgouchet
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {
    
    /** Android Log Tag */
    private static final String LOG_TAG = "AlarmReceiver";
    
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(LOG_TAG, "onReceive");
        context.startService(new Intent(context, UpdateService.class));
    }
}
