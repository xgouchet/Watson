package fr.xgouchet.webmonitor.diff;

public class Diff {

	private final DiffOperation mOperation;
	private final CharSequence mText;

	public Diff(DiffOperation operation, CharSequence text) {
		mOperation = operation;
		mText = text;
	}

	public static Diff insertDiff(CharSequence text) {
		return new Diff(DiffOperation.INSERT, text);
	}

	public static Diff deleteDiff(CharSequence text) {
		return new Diff(DiffOperation.DELETE, text);
	}

	public static Diff equalDiff(CharSequence text) {
		return new Diff(DiffOperation.EQUAL, text);
	}

	public DiffOperation getOperation() {
		return mOperation;
	}

	public CharSequence getText() {
		return mText;
	}
}
