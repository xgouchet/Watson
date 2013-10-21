package fr.xgouchet.webmonitor.ui.fragment;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.common.DB;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.data.TargetDAO;
import fr.xgouchet.webmonitor.provider.TargetContentProvider;
import fr.xgouchet.webmonitor.service.UpdateService;
import fr.xgouchet.webmonitor.ui.adapter.TargetAdapter;
import fr.xgouchet.webmonitor.ui.adapter.TargetAdapter.Listener;

public class TargetListFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, Listener {

	/** the Loader ID */
	private static final int LOADER_ID = 42;

	/** the cursor adapter */
	private TargetAdapter mAdapter;

	// ////////////////////////////////////////////////////////////////////////////////////
	// Fragment Lifecycle
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new TargetAdapter(getActivity(), null);
		mAdapter.setListener(this);
		setListAdapter(mAdapter);

		getListView().setEmptyView(getView().findViewById(android.R.id.empty));
		getListView().setItemsCanFocus(true);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Start loader from content provider
		getActivity().getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		boolean result = true;

		switch (item.getItemId()) {
		case R.id.action_add:
			addNewTarget();
			break;
		case R.id.action_sort_by_name:
			Settings.setSortMethod(Constants.SORT_BY_NAME, getActivity());
			restartLoader();
			break;
		case R.id.action_sort_by_last_update:
			Settings.setSortMethod(Constants.SORT_BY_LAST_UPDATE, getActivity());
			restartLoader();
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// TargetAdapter.Listener Implementation
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onActionRequested(final int actionIndex, final Target target) {

		//
		switch (actionIndex) {
		case 0:
			openTargetUrl(target);
			break;
		case 1:
			editTarget(target);
			break;
		case 2:
			deleteTarget(target);
			break;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// LoaderCallback Implementation
	// ////////////////////////////////////////////////////////////////////////////////////

	private void restartLoader() {
		getActivity().getLoaderManager().restartLoader(LOADER_ID, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		final String[] columns = new String[] { DB.TARGET.ID, DB.TARGET.URL,
				DB.TARGET.TITLE, DB.TARGET.LAST_UPDATE, DB.TARGET.LAST_CHECK,
				DB.TARGET.STATUS, DB.TARGET.DIFFERENCE };

		// set sort order
		final String sortOrder;

		switch (Settings.sSortMethod) {
		case Constants.SORT_BY_LAST_UPDATE:
			sortOrder = DB.TARGET.LAST_UPDATE + " DESC";
			break;
		case Constants.SORT_BY_NAME:
		default:
			sortOrder = DB.TARGET.TITLE + " ASC";
			break;
		}

		return new CursorLoader(getActivity(), TargetContentProvider.BASE_URI,
				columns, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
		data.setNotificationUri(getActivity().getContentResolver(),
				TargetContentProvider.BASE_URI);
		if (mAdapter != null) {
			mAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// Actions
	// ////////////////////////////////////////////////////////////////////////////////////

	private void addNewTarget() {

		Bundle args = new Bundle(1);
		args.putInt(Constants.EXTRA_COMMAND, Constants.CMD_CREATE);

		FragmentTransaction ft = getActivity().getFragmentManager()
				.beginTransaction();
		TargetFragment addTarget = new TargetFragment();
		addTarget.setArguments(args);
		addTarget.show(ft, "dialog");
	}

	private void openTargetUrl(final Target target) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(target.getUrl()));
		startActivity(intent);
	}

	/**
	 * Force check the target now
	 */
	private void checkTarget(final Target target) {

		Target fullTarget = TargetDAO.getInstance(getActivity()).getTarget(
				target.getUrl());

		Intent intent = new Intent(getActivity(), UpdateService.class);
		intent.setAction(Constants.ACTION_CHECK_TARGET);
		intent.putExtra(Constants.EXTRA_TARGET, fullTarget);
		getActivity().startService(intent);
	}

	/**
	 * 
	 */
	private void editTarget(final Target target) {
		Target fullTarget = TargetDAO.getInstance(getActivity()).getTarget(
				target.getUrl());

		Bundle args = new Bundle(1);
		args.putInt(Constants.EXTRA_COMMAND, Constants.CMD_EDIT);
		args.putParcelable(Constants.EXTRA_TARGET, fullTarget);

		FragmentTransaction ft = getActivity().getFragmentManager()
				.beginTransaction();
		TargetFragment addTarget = new TargetFragment();
		addTarget.setArguments(args);
		addTarget.show(ft, "dialog");
	}

	private void deleteTarget(final Target target) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(target.getTitle());

		builder.setMessage(getString(R.string.prompt_delete, target.getUrl()));

		builder.setNegativeButton(R.string.ui_delete,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						TargetDAO.getInstance(getActivity()).deleteTarget(
								target);
					}
				});
		builder.setNeutralButton(R.string.ui_cancel, null);

		builder.create().show();
	}
}
