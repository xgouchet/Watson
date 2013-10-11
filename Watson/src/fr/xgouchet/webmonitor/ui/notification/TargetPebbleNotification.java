package fr.xgouchet.webmonitor.ui.notification;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.utils.WatsonUtils;

public class TargetPebbleNotification {

	private static final String ACTION_PEBBLE_NOTIF = "com.getpebble.action.SEND_NOTIFICATION";

	public static void notifyTargetStatus(Context context, Target target) {
		final Intent i = new Intent(ACTION_PEBBLE_NOTIF);

		final Map<String, String> data = new HashMap<String, String>();

		// create title and body
		String title, body;
		if (target.getStatus() == Status.UPDATED) {
			title = "New content for : \"" + target.getTitle() + "\"";
			body = target.getDisplayDiff().toString();
		} else {
			title = "An error occured for : \"" + target.getTitle() + "\"";
			body = WatsonUtils.getErrorMessage(context, target.getStatus());
		}

		data.put("title", title);
		data.put("body", body);

		// convert to Json
		final JSONObject jsonData = new JSONObject(data);
		final String notificationData = new JSONArray().put(jsonData)
				.toString();

		i.putExtra("messageType", "PEBBLE_ALERT");
		i.putExtra("sender", context.getString(R.string.app_name));
		i.putExtra("notificationData", notificationData);

		context.sendBroadcast(i);
	}
}
