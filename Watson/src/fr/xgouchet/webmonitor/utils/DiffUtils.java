package fr.xgouchet.webmonitor.utils;

import java.util.LinkedList;
import java.util.List;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

public final class DiffUtils {

	public static List<Diff> getDiff(String old, String current) {

		String processedOld = process(old);
		String processCurrent = process(current);

		diff_match_patch dmp = new diff_match_patch();
		dmp.Diff_Timeout = 0;

		List<Diff> diffs = dmp.diff_main(processedOld, processCurrent, false);
		List<Diff> packed = new LinkedList<diff_match_patch.Diff>();

		int diffCount = diffs.size();
		if (diffCount > 1) {

			Diff previous = diffs.get(0);
			Diff diff = null;

			for (int i = 1; i < diffCount; ++i) {
				diff = diffs.get(i);

				if (diff.text.matches("\\s*")) {
					continue;
				} else if (diff.operation == previous.operation) {
					previous.text += diff.text;
				} else {
					packed.add(previous);
					previous = diff;
				}
			}

			packed.add(previous);
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
