package fr.xgouchet.webmonitor.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import fr.xgouchet.webmonitor.R;


public class MainActivity extends FragmentActivity {
    
    /** Android Log Tag */
    private static final String LOG_TAG = "MainActivity";
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Activity Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    
    
}
