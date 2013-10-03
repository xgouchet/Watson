package fr.xgouchet.webmonitor.utils;

import java.util.LinkedList;
import java.util.List;

import android.text.SpannableString;

public class SpannableBuilder {

	private final class SpanInfo {
		public int start, end;
		public Object span;
	}

	/** the string builder */
	private final StringBuilder mBuilder;
	/** the list of spans */
	private final List<SpanInfo> mSpans;

	/**
	 */
	public SpannableBuilder() {
		mBuilder = new StringBuilder();
		mSpans = new LinkedList<SpanInfo>();
	}

	/**
	 * Appends the contents of the specified string. If the string is null, then
	 * the string "null" is appended.
	 * 
	 * @param str
	 *            the string to append
	 */
	public void append(final String str) {
		mBuilder.append(str);
	}

	/**
	 * Appends the contents of the specified string. If the string is null, then
	 * the string "null" is appended.
	 * 
	 * @param str
	 *            the string to append
	 * @param span
	 *            the span to apply to the appended string
	 */
	public void append(final String str, final Object span) {
		int start, end;
		start = mBuilder.length();
		mBuilder.append(str);
		end = mBuilder.length();
		setSpan(span, start, end);
	}

	/**
	 * Sets a span info for the whole text. If text is appended, the given span
	 * won't take it into account
	 * 
	 * @param span
	 *            the span object
	 */
	public void setSpan(final Object span) {
		setSpan(span, 0, mBuilder.length());
	}

	/**
	 * Sets a span info for a range of text.
	 * 
	 * @param span
	 *            the span object
	 * @param start
	 *            the start index
	 * @param end
	 *            the end index
	 */
	public void setSpan(final Object span, final int start, final int end) {
		SpanInfo info;
		info = new SpanInfo();
		info.span = span;
		info.start = start;
		info.end = end;
		mSpans.add(info);
	}

	/**
	 * @return a spannable string from this {@link XMLSpanBuilder} content
	 */
	public SpannableString buildString() {
		SpannableString string;
		string = new SpannableString(mBuilder.toString());

		for (SpanInfo info : mSpans) {
			string.setSpan(info.span, info.start, info.end, 0);
		}

		return string;
	}

}
