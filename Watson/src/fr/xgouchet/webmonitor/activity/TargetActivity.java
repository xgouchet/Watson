package fr.xgouchet.webmonitor.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import fr.xgouchet.webmonitor.ui.fragment.TargetFragment;


public class TargetActivity extends FragmentActivity {
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Activity Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // start dialog fragment
        FragmentManager mgr = getSupportFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        TargetFragment target = new TargetFragment();
        target.show(transaction, "");
        
        // finish activity when the fragment closes
        target.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss(final DialogInterface dialog) {
                finish();
            }
        });
    }
}
