package fr.xgouchet.webmonitor.activity;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import fr.xgouchet.webmonitor.R;


public class MainActivity extends Activity implements LoaderCallbacks<Cursor> {
    
    private static final int LOADER_ID = 42;
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Activity Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // TODO setup list
        
        // Start loader from content provider
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // LoaderCallback Implementation
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    @Override
    public void onLoadFinished(final Loader<Cursor> arg0, final Cursor arg1) {
        // TODO Auto-generated method stub
        
    }
    
    
    @Override
    public void onLoaderReset(final Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        
    }
    
}
