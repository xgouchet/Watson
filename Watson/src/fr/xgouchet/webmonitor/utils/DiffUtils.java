package fr.xgouchet.webmonitor.utils;

import java.util.List;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import android.util.Log;

public final class DiffUtils {

	public static List<Diff> getDiff(String old, String current) {

		String processedOld = process(old);
		String processCurrent = process(current);

		diff_match_patch dmp = new diff_match_patch();
		dmp.Diff_Timeout = 0;

		List<Diff> diffs = dmp.diff_main(processedOld, processCurrent, false);

		if (diffs.size() > 1) {
			for (Diff diff : diffs) {
				Log.i("DIFF", diff.toString());
			}
		}

		return diffs;
	}

	private static String process(String input) {
		String output;

		output = input.trim().replaceAll("\\s+", " ");

		// TODO prefs to
		// - ignore case ?
		// - ignore HTML tags ?

		return output;
	}

	/**
	 * Private constructor
	 */
	private DiffUtils() {
	}
}
