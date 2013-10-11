package fr.xgouchet.webmonitor.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Toast;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.common.DB;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.data.TargetDAO;
import fr.xgouchet.webmonitor.provider.TargetContentProvider;
import fr.xgouchet.webmonitor.ui.notification.TargetNotification;
import fr.xgouchet.webmonitor.ui.notification.TargetPebbleNotification;
import fr.xgouchet.webmonitor.utils.DiffUtils;
import fr.xgouchet.webmonitor.utils.SpannableBuilder;
import fr.xgouchet.webmonitor.utils.WatsonUtils;
import fr.xgouchet.webmonitor.utils.WebUtils;

public class UpdateService extends IntentService {

	/** Android Log Tag */
	private static final String LOG_TAG = "UpdateService";

	private List<Target> mPendingUpdate, mUpdated;
	private TargetDAO mTargetDao;
	private NotificationManager mNotifMgr;

	public UpdateService() {
		super("Watson/UpdateService");

		mPendingUpdate = new ArrayList<Target>();
		mUpdated = new ArrayList<Target>();

	}

	@Override
	public void onCreate() {
		super.onCreate();

		mTargetDao = TargetDAO.getInstance(this);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {

		Log.d(LOG_TAG, "onHandleIntent");

		// Check internet is available
		if (!WebUtils.isInternetAvailable(this)) {
			Log.i("Watson", "No internet available, ignore update");
			if (Constants.ACTION_CHECK_TARGET.equals(intent.getAction())) {
				Toast.makeText(
						this,
						"Internet is not available, page will be checked when network will be available again",
						Toast.LENGTH_LONG).show();
			}
			return;
		}

		// Trigger updates
		if (Constants.ACTION_CHECK_TARGET.equals(intent.getAction())) {

			Target target = intent.getParcelableExtra(Constants.EXTRA_TARGET);

			if (target != null) {
				updateTarget(target, System.currentTimeMillis());
			}

		} else {
			updateAll();
		}

	}

	/**
	 * Update all targets in database
	 */
	private void updateAll() {
		Log.d(LOG_TAG, "updateAll");
		fetchTargets();
		updateTargets();
		notifyTargets();
	}

	/**
	 * Fetch a list of targets to update
	 */
	private void fetchTargets() {
		Log.d(LOG_TAG, "fetchTargets");

		Cursor cursor = getContentResolver().query(
				TargetContentProvider.BASE_URI, null, selectTargetsToUpdate(),
				null, null);

		while (cursor.moveToNext()) {
			mPendingUpdate.add(TargetDAO.buildTargetFromCursor(cursor));
		}

		cursor.close();
	}

	/**
	 * Update all targets in need of update
	 */
	private void updateTargets() {
		Log.d(LOG_TAG, "updateTargets");

		long now = System.currentTimeMillis();

		for (Target target : mPendingUpdate) {
			if (!WebUtils.isInternetAvailable(this)) {
				break;
			}

			updateTarget(target, now);
		}
	}

	/**
	 * Creates a select query to find targets to update
	 * 
	 * @return the selection string to select targets needing an update right
	 *         now
	 */
	private String selectTargetsToUpdate() {
		final long now = System.currentTimeMillis();

		StringBuilder selection = new StringBuilder();

		selection.append('(');

		selection.append(DB.TARGET.LAST_CHECK);
		selection.append('+');
		selection.append(DB.TARGET.FREQUENCY);
		selection.append('>');
		selection.append(now);

		selection.append(") OR (");

		selection.append(DB.TARGET.LAST_CHECK);
		selection.append("=0");

		selection.append(')');

		// FIXME debug , lets check all
		return null;// selection.toString();
	}

	/**
	 * Update a single target
	 * 
	 * @param target
	 * @param now
	 */
	private void updateTarget(final Target target, final long now) {
		Log.i(LOG_TAG, "updateTarget " + target);
		int oldStatus = target.getStatus();

		// get online content
		String content = WebUtils.getTargetContent(this, target);
		if (content != null) {
			WebUtils.ensureFaviconExists(this, target);
			checkContentUpdate(target, content, now);
		}

		target.setLastCheck(now);

		// updates in db and notif
		mTargetDao.updateTarget(target);
		if ((target.getStatus() != oldStatus)
				|| (target.getStatus() == Status.UPDATED)) {
			mUpdated.add(target);
		}
	}

	/**
	 * 
	 * @param target
	 * @param content
	 * @param now
	 */
	private void checkContentUpdate(final Target target, final String content,
			final long now) {
		Log.d(LOG_TAG, "checkContentUpdate " + target);
		target.setStatus(Status.OK);

		String oldContent = target.getContent();
		if ((target.getLastCheck() == 0) || (oldContent == null)) {
			target.setContent(content);
		} else {
			StringBuilder logBuilder = new StringBuilder();
			List<Diff> diffs = DiffUtils.getDiff(oldContent, content);

			// prepare display string
			SpannableBuilder displayDiff = new SpannableBuilder();

			// compute length of diff
			int totalDiff = 0;
			int minDiff = target.getMinimumDifference();
			boolean eq = false;
			for (Diff diff : diffs) {

				if (diff.operation == Operation.EQUAL) {
					if (!eq) {
						displayDiff.append("[...]", new StyleSpan(
								Typeface.ITALIC));
						eq = true;
					}

					logBuilder.append(diff.toString());
					logBuilder.append('\n');
				} else if (diff.operation == Operation.INSERT) {
					int size = diff.text.length();
					if (size > minDiff) {
						totalDiff += size;

						logBuilder.append(diff.toString());
						logBuilder.append('\n');

						displayDiff.append(diff.text, new StyleSpan(
								Typeface.BOLD));
						eq = false;

					} else {
						logBuilder.append('(');
						logBuilder.append(diff.toString());
						logBuilder.append(')');
						logBuilder.append('\n');
					}
				} else {
					logBuilder.append('(');
					logBuilder.append(diff.toString());
					logBuilder.append(')');
					logBuilder.append('\n');
				}
			}

			target.setDisplayDiff(displayDiff.buildString());

			if (totalDiff > 0) {
				target.setContent(content);
				target.setStatus(Status.UPDATED);
				target.setLastUpdate(now);
			}

			// log
			File logDir = WatsonUtils.getLogDir(this);
			File path = new File(logDir, target.getTargetId() + ".log");
			WatsonUtils.writeTextFile(path.getPath(), logBuilder.toString(),
					"UTF-8");
		}
	}

	/**
	 * Creates a notification for target status
	 * 
	 */
	private void notifyTargets() {
		mNotifMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		for (Target target : mUpdated) {
			if (target.getStatus() != Status.OK) {
				TargetNotification.notifyTargetStatus(this, mNotifMgr, target);
			} else {
				TargetNotification.clearErrorNotifications(mNotifMgr,
						target.getUrl());
			}
		}

		if (Settings.sNotifyPebble) {
			for (Target target : mUpdated) {
				if (target.getStatus() != Status.OK) {
					TargetPebbleNotification.notifyTargetStatus(this, target);
				}
			}
		}
	}
}
