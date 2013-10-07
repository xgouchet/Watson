package fr.xgouchet.webmonitor.common;

public final class Constants {

	// ////////////////////////////////////////////////////////////////////////////////////
	// Time
	// ////////////////////////////////////////////////////////////////////////////////////

	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	public static final long WEEK = DAY * 7;
	public static final long MONTH = DAY * 28;

	public static final long[] FREQUENCIES = new long[] { DAY / 2, DAY,
			WEEK / 2, WEEK, MONTH / 2, MONTH };

	public static final int[] DIFFERENCES = new int[] { 1, 5, 8, 13, 21, 34,
			55, 89, 144, 233, 377, 610, };

	// ////////////////////////////////////////////////////////////////////////////////////
	// Preferences
	// ////////////////////////////////////////////////////////////////////////////////////

	public static final String PREFERENCES_NAME = "fr.xgouchet.websiteupdate";

	public static final String PREF_BLINK_LED = "blink_led";
	public static final String PREF_LED_COLOR = "led_color";
	public static final String PREF_BLINK_LED_ERROR = "blink_led_error";
	public static final String PREF_LED_ERROR_COLOR = "led_error_color";
	public static final String PREF_WIFI_ONLY = "wifi_only";
	public static final String PREF_ALLOW_ROAMING = "allow_roaming";
	public static final String PREF_DEFAULT_FREQUENCY = "default_frequency";
	public static final String PREF_DEFAULT_DIFFERENCE = "default_difference";

	// ////////////////////////////////////////////////////////////////////////////////////
	// Actions
	// ////////////////////////////////////////////////////////////////////////////////////

	public static final String EXTRA_COMMAND = "action";
	public static final String EXTRA_TARGET = "target";

	public static final String ACTION_EDIT_TARGET = "fr.xgouchet.webmonitor.ACTION_EDIT_TARGET";
	public static final String ACTION_NEW_TARGET = "fr.xgouchet.webmonitor.ACTION_NEW_TARGET";
	public static final String ACTION_CHECK_TARGET = "fr.xgouchet.webmonitor.ACTION_CHECK_TARGET";

	public static final int CMD_CREATE = 1;
	public static final int CMD_EDIT = 2;
	public static final int CMD_DELETE = 3;

	private Constants() {
	}
}
