package fr.xgouchet.webmonitor.ui.fragment;

import java.util.LinkedList;
import java.util.List;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.activity.TargetActivity;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.common.DB;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.data.TargetDAO;
import fr.xgouchet.webmonitor.provider.TargetContentProvider;
import fr.xgouchet.webmonitor.ui.adapter.TargetAdapter;

public class TargetListFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, OnItemLongClickListener, Callback {

	/** the Loader ID */
	private static final int LOADER_ID = 42;

	/** the cursor adapter */
	private SimpleCursorAdapter mAdapter;

	/** Used for context menu */
	private List<Target> mSelectedTargets;

	// ////////////////////////////////////////////////////////////////////////////////////
	// Fragment Lifecycle
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
		setListAdapter(mAdapter);

		// TODO on long click : action mode
		getListView().setEmptyView(getView().findViewById(android.R.id.empty));
		getListView().setOnItemLongClickListener(this);

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
		case R.id.menu_add:
			addNewTarget();
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// ListFragment Implementation
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		super.onListItemClick(l, v, position, id);

		// TODO if action mode : add / remove from action mode

		CursorWrapper cursor = (CursorWrapper) mAdapter.getItem(position);
		Target target = TargetDAO.buildTargetFromCursor(cursor);

		Intent intent = new Intent(getActivity(), TargetActivity.class);
		intent.setAction(Constants.ACTION_EDIT_TARGET);
		intent.putExtra(Constants.EXTRA_TARGET, target);
		startActivity(intent);
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// OnItemLongClickListener Implementation
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		CursorWrapper cursor = (CursorWrapper) mAdapter.getItem(position);
		Target target = TargetDAO.buildTargetFromCursor(cursor);

		if (mActionModeActive) {
			return false;
		}

		mSelectedTargets = new LinkedList<Target>();
		mSelectedTargets.add(target);

		mActionMode = getActivity().startActionMode(this);

		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// ActionMode Implementation
	// ////////////////////////////////////////////////////////////////////////////////////

	private ActionMode mActionMode;
	private boolean mActionModeActive;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {

		getActivity().getMenuInflater().inflate(R.menu.context_target, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub

	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// LoaderCallback Implementation
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		final String[] columns = new String[] { DB.TARGET.ID, DB.TARGET.URL,
				DB.TARGET.TITLE, DB.TARGET.LAST_UPDATE, DB.TARGET.LAST_CHECK,
				DB.TARGET.STATUS, DB.TARGET.DIFFERENCE };

		// TODO add sort preference
		final String sortOrder = DB.TARGET.TITLE + " ASC";

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
	//
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
}
