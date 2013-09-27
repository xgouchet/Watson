package fr.xgouchet.webmonitor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.ui.fragment.TargetListFragment;


public class MainActivity extends FragmentActivity {
    
    public static final String TAG_LIST = "LIST";
    public static final String TAG_PREFS = "PREFS";
    public static final String TAG_ABOUT = "ABOUT";
    public static final String TAG_HELP = "HELP";
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Activity Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        
        String action = getIntent().getAction();
        Log.i("Action", action);
        
        // deep link 
        if (Intent.ACTION_MANAGE_NETWORK_USAGE.equals(action)) {
            showSettings();
        } else {
            showTargetList();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        SharedPreferences prefs;
        prefs = getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        Settings.updateFromPreferences(prefs);
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        
        getMenuInflater().inflate(R.menu.activity_main, menu);
        
        
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(0).getName();
            Log.i("TOTO", tag);
        } else {
            Log.i("TOTO", "null");
        }
        
        
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean result = true;
        
        switch (item.getItemId()) {
            case R.id.menu_settings:
                showSettings();
                break;
            case R.id.menu_about:
                showAbout();
                break;
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        
        return result;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Fragment Utils
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void showTargetList() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        
        Fragment fragment = new TargetListFragment();
        
        transaction.add(android.R.id.primary, fragment, TAG_LIST);
        transaction.commit();
        
        invalidateOptionsMenu();
    }
    
    private void showSettings() {
        
    }
    
    private void showAbout() {
        
    }
    
}
