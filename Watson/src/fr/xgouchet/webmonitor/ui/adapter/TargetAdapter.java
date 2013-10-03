package fr.xgouchet.webmonitor.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.common.DB;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.data.TargetDAO;
import fr.xgouchet.webmonitor.utils.WatsonUtils;

public class TargetAdapter extends SimpleCursorAdapter {

	public interface Listener {
		void onActionRequested(int actionIndex, Target target);
	}

	private Listener mListener;

	private class ViewHolder implements OnClickListener {
		private ImageView mIcon;
		private TextView mTitle, mLastCheck, mLastUpdated;
		private View mAction1, mAction2, mAction3;

		private Target mTarget;

		public ViewHolder(View root) {
			mIcon = (ImageView) root.findViewById(android.R.id.icon);
			mTitle = (TextView) root.findViewById(android.R.id.title);
			mLastCheck = (TextView) root.findViewById(android.R.id.text1);
			mLastUpdated = (TextView) root.findViewById(android.R.id.text2);
			mAction1 = root.findViewById(android.R.id.button1);
			mAction2 = root.findViewById(android.R.id.button2);
			mAction3 = root.findViewById(android.R.id.button3);

			mAction1.setOnClickListener(this);
			mAction2.setOnClickListener(this);
			mAction3.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			int action = -1;
			switch (view.getId()) {
			case android.R.id.button1:
				action = 0;
				break;
			case android.R.id.button2:
				action = 1;
				break;
			case android.R.id.button3:
				action = 2;
				break;
			}

			if (mListener != null) {
				mListener.onActionRequested(action, mTarget);
			}
		}

	}

	protected Context mAppContext;

	/**
	 * @param context
	 *            the current application context
	 */
	public TargetAdapter(final Context context, final Cursor cursor) {

		super(context, R.layout.item_target, cursor,
				new String[] { DB.TARGET.TITLE },
				new int[] { android.R.id.title }, 0);
		mAppContext = context;
	}

	/**
	 * @param listener
	 *            the listener
	 */
	public void setListener(Listener listener) {
		mListener = listener;
	}

	/**
	 * @see android.widget.SimpleCursorAdapter#bindView(android.view.View,
	 *      android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(final View view, final Context context,
			final Cursor cursor) {

		if (cursor.getColumnIndex(DB.TARGET.TITLE) < 0) {
			return;
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder(view);
			view.setTag(holder);
		}

		Target target = TargetDAO.buildTargetFromCursor(cursor);
		holder.mTarget = target;

		// Title
		holder.mTitle.setText(target.getTitle());

		// Last check
		holder.mLastCheck.setText(WatsonUtils.getLastCheckTime(mAppContext,
				target.getLastCheck()));

		// Last Update / Error
		if (target.getStatus() < Status.HTTP_ERROR) {
			holder.mLastUpdated.setText(WatsonUtils.getLastUpdateTime(
					mAppContext, target.getLastUpdate()));
		} else {
			holder.mLastUpdated.setText(WatsonUtils.getErrorMessage(
					mAppContext, target.getStatus()));
		}

		// icon
		Bitmap bmp = WatsonUtils.getTargetIcon(mAppContext, target);
		if (bmp == null) {
			holder.mIcon.setImageResource(R.drawable.ic_favicon);
		} else {
			holder.mIcon.setImageBitmap(bmp);
		}
	}

}
