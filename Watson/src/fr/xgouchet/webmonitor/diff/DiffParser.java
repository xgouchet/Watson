package fr.xgouchet.webmonitor.diff;

import java.util.LinkedList;
import java.util.List;

public class DiffParser {

	private final long mTimeout;
	private boolean mIgnoreWS;
	private long mDeadline;

	private CharSequence mOld, mCurrent;
	private List<Diff> mDiffs;

	/**
	 * @param timeout
	 *            the time (in ms) before killing the diff process to avoid
	 *            using too much process
	 */
	public DiffParser(long timeout) {
		mTimeout = timeout;
		mIgnoreWS = false;
	}

	/**
	 * @param ignoreWS
	 *            ignore multiple whitespaces (HTML)
	 */
	public void setIgnoreWS(boolean ignoreWS) {
		mIgnoreWS = ignoreWS;
	}

	/**
	 * Generates a diff between the two files
	 * 
	 * @param old
	 *            a non null string
	 * @param current
	 *            a non null string
	 * @param ignoreWhitespaces
	 *            if true, differences in whitespaces of all kinds will be
	 *            ignored (usefull for HTML)
	 * 
	 * @return the list of operation to perform on the old String to get to the
	 *         new String
	 */
	public List<Diff> diff(CharSequence old, CharSequence current) {

		// failsafe : null
		if ((old == null) || (current == null)) {
			throw new IllegalArgumentException(
					"Null strings, can't compute diff");
		}

		List<Diff> diffs = new LinkedList<Diff>();

		// Speedups
		if (old.length() == 0) {
			diffs.add(Diff.insertDiff(current));
		} else if (current.length() == 0) {
			diffs.add(Diff.deleteDiff(old));
		} else if (current.equals(old)) {
			diffs.add(Diff.equalDiff(old));
		}
		if (diffs.size() > 0) {
			return diffs;
		}

		// lets go for a ride !
		mOld = old;
		mCurrent = current;
		if (mTimeout > 0) {
			mDeadline = System.currentTimeMillis() + mTimeout;
		} else {
			mDeadline = Long.MAX_VALUE;
		}

		mDiffs = new LinkedList<Diff>();

		diff();
		return mDiffs;
	}

	/**
	 * Perform a Diff on whatever charsequence is in memory
	 */
	private void diff() {
		checkCommonPrefix();

	}

	/**
	 * 
	 */
	private void checkCommonPrefix() {
		int iOld = 0, iCurrent = 0;
		int lOld = mOld.length();
		int lCurrent = mCurrent.length();
		char cOld, cCurrent;
		boolean stop = false;
		boolean ws = false;

		while (!stop) {
			cOld = mOld.charAt(iOld);
			cCurrent = mCurrent.charAt(iCurrent);
			if (cOld == cCurrent) {
				iOld++;
				iCurrent++;
				ws = isWhiteSpace(cOld);
			} else if (isWhiteSpace(cOld) && isWhiteSpace(cCurrent)) {
				iOld++;
				iCurrent++;
				ws = true;
			} else if (ws) {
				if (isWhiteSpace(cOld)) {
					iOld++;
				} else if (isWhiteSpace(cCurrent)) {
					iCurrent++;
				} else {
					stop = true;
				}
			} else {
				if (isWhiteSpace(cOld)
						&& ((iCurrent == 0) || (iCurrent >= lCurrent - 1))) {
					iOld++;
					ws = true;
				} else if (isWhiteSpace(cCurrent)
						&& ((iOld == 0) || (iOld >= lOld - 1))) {
					iCurrent++;
					ws = true;
				} else {
					stop = true;
					ws = false; 
				}
			}

			// stop when we reach end of string
			if (ws) {
				stop |= (iOld == lOld) && (iCurrent == lCurrent);
			} else {
				stop |= iOld == lOld;
				stop |= iCurrent == lCurrent;
			}
		}

		iOld--;
		iCurrent--;

		mDiffs.add(Diff.equalDiff(mCurrent.subSequence(0, iCurrent)));

		mCurrent = mCurrent.subSequence(iCurrent, mCurrent.length());
		mOld = mOld.subSequence(iOld, mOld.length());
	}

	private boolean isWhiteSpace(char c) {
		return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
	}
}
