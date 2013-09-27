package fr.xgouchet.webmonitor.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.data.DB;
import fr.xgouchet.webmonitor.provider.TargetContentProvider;
import fr.xgouchet.webmonitor.ui.adapter.TargetAdapter;


public class TargetListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    
    
    /** the Loader ID */
    private static final int LOADER_ID = 42;
    
    /** the cursor adapter */
    private CursorAdapter mAdapter;
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Fragment Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // Start loader from content provider
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        
        mAdapter = new TargetAdapter(getActivity(), null);
        setListAdapter(mAdapter);
        
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        
        
        return view;
    }
    
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        getListView().setOnCreateContextMenuListener(this);
        getListView().setEmptyView(view.findViewById(android.R.id.empty));
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
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // LoaderCallback Implementation
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        final String[] columns = new String[] {
                DB.TARGET.ID,
                DB.TARGET.URL,
                DB.TARGET.TITLE,
                DB.TARGET.LAST_UPDATE,
                DB.TARGET.LAST_CHECK,
                DB.TARGET.STATUS,
                DB.TARGET.DIFFERENCE
        };
        
        // TODO add sort preference
        final String sortOrder = DB.TARGET.TITLE + " ASC";
        
        return new CursorLoader(getActivity(), TargetContentProvider.BASE_URI, columns,
                null, null, sortOrder);
    }
    
    
    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        data.setNotificationUri(getActivity().getContentResolver(), TargetContentProvider.BASE_URI);
        mAdapter.swapCursor(data);
    }
    
    
    @Override
    public void onLoaderReset(final Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // 
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void addNewTarget() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        TargetFragment addTarget = new TargetFragment();
        addTarget.show(ft, "dialog");
    }
}
