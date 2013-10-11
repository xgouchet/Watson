package fr.xgouchet.webmonitor.ui.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.activity.TargetActivity;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.utils.WatsonUtils;

public class TargetNotification {

	public static final int NOTIF_UPDATE = 42;

	public static final int NOTIF_ERROR = 666;

	public static void clearAllNotifications(final Context context,
			final Target target) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		clearErrorNotifications(manager, target.getUrl());
		clearUpdateNotifications(manager, target.getUrl());
	}

	public static void clearAllNotifications(final Context context,
			final String url) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		clearErrorNotifications(manager, url);
		clearUpdateNotifications(manager, url);
	}

	/**
	 * @param manager
	 * @param target
	 */
	public static void clearErrorNotifications(
			final NotificationManager manager, final String url) {
		manager.cancel(url, NOTIF_ERROR);
	}

	/**
	 * @param manager
	 * @param target
	 */
	public static void clearUpdateNotifications(
			final NotificationManager manager, final String url) {
		manager.cancel(url, NOTIF_UPDATE);
	}

	/**
	 * Creates a notification on the status of a target
	 * 
	 * @param target
	 *            the target
	 */
	public static void notifyTargetStatus(final Context context,
			final NotificationManager notificationManager, final Target target) {
		if (target.getStatus() == Status.UPDATED) {
			notificationManager.cancel(target.getUrl(), NOTIF_UPDATE);
			notificationManager.cancel(target.getUrl(), NOTIF_ERROR);
		} else {
			notificationManager.cancel(target.getUrl(), NOTIF_ERROR);
		}

		int id;

		Notification notif;

		if (target.getStatus() == Status.UPDATED) {
			notif = buildUpdateNotification(context, target);

			id = NOTIF_UPDATE;
		} else {
			notif = buildErrorNotification(context, target);
			id = NOTIF_ERROR;
		}

		notificationManager.notify(target.getUrl(), id, notif);
	}

	/**
	 * Builds a notification that this page content has changed
	 * 
	 * @param context
	 *            the current application context
	 * @param target
	 *            the target
	 * @return the notification
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private static Notification buildUpdateNotification(Context context,
			final Target target) {
		Builder builder = new Builder(context);

		String smallMessage = "New content for : \"" + target.getTitle() + "\"";

		// Basic notif params
		builder.setWhen(System.currentTimeMillis());
		builder.setContentTitle(target.getTitle());
		builder.setAutoCancel(true);
		builder.setLargeIcon(WatsonUtils.getTargetIcon(context, target));

		builder.setSmallIcon(R.drawable.ic_stat_sync);
		builder.setContentText(smallMessage);

		// Led blink ?
		if (Settings.sBlinkLed) {
			builder.setLights(Settings.sBlinkLedColor, (int) Constants.SECOND,
					(int) Constants.SECOND * 15);
		}

		// pending intent
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(target.getUrl()));
		builder.setContentIntent(PendingIntent.getActivity(context, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK));

		// Android JB big notification
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			Notification.BigTextStyle bigTextStyle = new BigTextStyle(builder);
			bigTextStyle.setSummaryText(smallMessage);
			bigTextStyle.bigText(target.getDisplayDiff());

			return bigTextStyle.build();
		} else {
			return builder.getNotification();
		}
	}

	/**
	 * Builds a notification that this page content could not be checked
	 * 
	 * @param context
	 *            the current application context
	 * @param target
	 *            the target
	 * @return the notification
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	private static Notification buildErrorNotification(final Context context,
			final Target target) {

		Builder builder = new Builder(context);

		// Basic notif params
		builder.setWhen(System.currentTimeMillis());
		builder.setContentTitle(target.getTitle());
		builder.setAutoCancel(true);
		builder.setLargeIcon(WatsonUtils.getTargetIcon(context, target));

		builder.setSmallIcon(R.drawable.ic_stat_sync_error);
		builder.setContentText("An error occured");

		// Led blink ?
		if (Settings.sBlinkLedError) {
			builder.setLights(Settings.sBlinkLedErrorColor,
					(int) Constants.SECOND, (int) Constants.SECOND * 15);
		}

		// Pending intent
		Intent intent = new Intent(context, TargetActivity.class);
		intent.setAction(Constants.ACTION_EDIT_TARGET);
		intent.putExtra(Constants.EXTRA_TARGET, target);
		intent.putExtra(Constants.EXTRA_COMMAND, Constants.CMD_EDIT);
		builder.setContentIntent(PendingIntent.getActivity(context, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK));

		// Android JB big notification
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			BigTextStyle bigTextStyle = new BigTextStyle(builder);
			bigTextStyle.bigText(WatsonUtils.getErrorMessage(context,
					target.getStatus()));

			return bigTextStyle.build();
		} else {
			return builder.getNotification();
		}
	}
}
