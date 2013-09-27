package fr.xgouchet.webmonitor.ui.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.activity.MainActivity;
import fr.xgouchet.webmonitor.activity.TargetActivity;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.utils.WatsonUtils;


public class TargetNotification {
    
    public static final int NOTIF_UPDATE = 42;
    
    public static final int NOTIF_ERROR = 666;
    
    public static void clearAllNotifications(final Context context, final Target target) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        clearErrorNotifications(manager, target.getUrl());
        clearUpdateNotifications(manager, target.getUrl());
    }
    
    public static void clearAllNotifications(final Context context, final String url) {
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        clearErrorNotifications(manager, url);
        clearUpdateNotifications(manager, url);
    }
    
    /**
     * @param manager
     * @param target
     */
    public static void clearErrorNotifications(final NotificationManager manager,
            final String url) {
        manager.cancel(url, NOTIF_ERROR);
    }
    
    /**
     * @param manager
     * @param target
     */
    public static void clearUpdateNotifications(final NotificationManager manager,
            final String url) {
        manager.cancel(url, NOTIF_UPDATE);
    }
    
    /**
     * Creates a notification on the status of a target
     * 
     * @param target
     *            the target
     */
    public static void notifyTargetStatus(final Context context,
            final NotificationManager notif, final Target target) {
        if (target.getStatus() == Status.UPDATED) {
            notif.cancel(target.getUrl(), NOTIF_UPDATE);
        } else {
            notif.cancel(target.getUrl(), NOTIF_ERROR);
        }
        
        int id;
        Intent intent;
        Builder builder = new Builder(context);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(target.getTitle());
        builder.setAutoCancel(true);
        builder.setLargeIcon(WatsonUtils.getTargetIcon(context, target));
        
        if (target.getStatus() == Status.UPDATED) {
            intent = buildUpdateNotification(builder, target);
            
            id = NOTIF_UPDATE;
        } else {
            intent = buildErrorNotification(context, builder, target);
            
            id = NOTIF_ERROR;
        }
        
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent,
                Intent.FLAG_ACTIVITY_NEW_TASK));
        
        notif.notify(target.getUrl(), id, builder.build());
    }
    
    /**
     * 
     * @param builder
     * @param target
     * @return
     */
    private static Intent buildUpdateNotification(final Builder builder,
            final Target target) {
        builder.setSmallIcon(R.drawable.stat_notify_sync);
        builder.setContentText("Some new content exists for the page \""
                + target.getTitle() + "\"");
        
        if (Settings.sBlinkLed) {
            builder.setLights(Settings.sBlinkLedColor, (int) Constants.SECOND,
                    (int) Constants.SECOND * 15);
        }
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(target.getUrl()));
        
        return intent;
    }
    
    private static Intent buildErrorNotification(final Context context,
            final Builder builder, final Target target) {
        builder.setSmallIcon(R.drawable.stat_notify_sync_error);
        builder.setContentText(WatsonUtils.getErrorMessage(context,
                target.getStatus()));
        
        if (Settings.sBlinkLedError) {
            builder.setLights(Settings.sBlinkLedErrorColor, (int) Constants.SECOND,
                    (int) Constants.SECOND * 15);
        }
        
        Intent intent = new Intent(context, TargetActivity.class);
        intent.setAction(Constants.ACTION_EDIT_TARGET);
        intent.putExtra(Constants.EXTRA_TARGET, target);
        intent.putExtra(Constants.EXTRA_COMMAND, Constants.CMD_EDIT);
        
        return intent;
    }
}
