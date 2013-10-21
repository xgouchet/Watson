package fr.xgouchet.webmonitor.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

public final class Settings {

	// Notifications
	public static boolean sBlinkLed = false;
	public static int sBlinkLedColor = Color.WHITE;
	public static boolean sBlinkLedError = false;
	public static int sBlinkLedErrorColor = Color.RED;
	public static boolean sNotifyPebble = false;

	// Network
	public static boolean sWifiOnly = false;
	public static boolean sAllowRoaming = false;

	// Defaults
	public static int sDefaultFrequency = 0;
	public static int sDefaultDifference = 10;

	// Sort methods
	public static int sSortMethod = Constants.SORT_BY_NAME;

	public static void setSortMethod(final int method, final Context context) {
		sSortMethod = method;

		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt(Constants.PREF_SORT_METHOD, method);
	}

	public static void updateFromPreferences(final SharedPreferences preferences) {
		sBlinkLed = preferences.getBoolean(Constants.PREF_BLINK_LED, false);
		sBlinkLedError = preferences.getBoolean(Constants.PREF_BLINK_LED_ERROR,
				false);
		sBlinkLedColor = getColorPreference(
				preferences.getString(Constants.PREF_LED_COLOR, "white"),
				Color.WHITE);
		sBlinkLedErrorColor = getColorPreference(
				preferences.getString(Constants.PREF_LED_ERROR_COLOR, "red"),
				Color.RED);

		sNotifyPebble = preferences.getBoolean(Constants.PREF_PEBBLE_WATCH,
				false);

		sWifiOnly = preferences.getBoolean(Constants.PREF_WIFI_ONLY, false);
		sAllowRoaming = preferences.getBoolean(Constants.PREF_ALLOW_ROAMING,
				false);

		sDefaultFrequency = getIntPreference(
				preferences.getString(Constants.PREF_DEFAULT_FREQUENCY, "0"), 0);
		sDefaultDifference = getIntPreference(
				preferences.getString(Constants.PREF_DEFAULT_DIFFERENCE, "10"),
				10);

		sSortMethod = preferences.getInt(Constants.PREF_SORT_METHOD, 0);
	}

	public static int getIntPreference(final String value, final int def) {
		int pref;
		try {
			pref = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			pref = def;
		}
		return pref;
	}

	public static int getColorPreference(final String value, final int def) {
		int color;
		if ("white".equals(value)) {
			color = Color.WHITE;
		} else if ("red".equals(value)) {
			color = Color.RED;
		} else if ("orange".equals(value)) {
			color = Color.argb(255, 255, 128, 0);
		} else if ("yellow".equals(value)) {
			color = Color.YELLOW;
		} else if ("green".equals(value)) {
			color = Color.GREEN;
		} else if ("cyan".equals(value)) {
			color = Color.CYAN;
		} else if ("blue".equals(value)) {
			color = Color.BLUE;
		} else if ("magenta".equals(value)) {
			color = Color.MAGENTA;
		} else if ("violet".equals(value)) {
			color = Color.argb(255, 128, 0, 255);
		} else {
			color = 0;
		}
		return color;
	}

	private Settings() {
	}

}
