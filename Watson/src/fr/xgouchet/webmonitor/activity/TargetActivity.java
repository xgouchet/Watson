package fr.xgouchet.webmonitor.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.ui.fragment.TargetFragment;


public class TargetActivity extends FragmentActivity {
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Activity Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        
        String action = intent.getAction();
        Bundle args = new Bundle();
        
        if (Constants.ACTION_EDIT_TARGET.equals(action)) {
            args.putInt(Constants.EXTRA_COMMAND, Constants.CMD_EDIT);
            args.putParcelable(Constants.EXTRA_TARGET,
                    intent.getParcelableExtra(Constants.EXTRA_TARGET));
        } else if (Intent.ACTION_SEND.equals(action)) {
            args.putInt(Constants.EXTRA_COMMAND, Constants.CMD_CREATE);
            Target target = new Target();
            target.setUrl(intent.getStringExtra(Intent.EXTRA_TEXT));
            target.setTitle(intent.getStringExtra(Intent.EXTRA_SUBJECT));
        } else {
            args.putInt(Constants.EXTRA_COMMAND, Constants.CMD_CREATE);
        }
        
        // start dialog fragment
        FragmentManager mgr = getSupportFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        TargetFragment target = new TargetFragment();
        target.setArguments(args);
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
