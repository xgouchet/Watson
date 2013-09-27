package fr.xgouchet.webmonitor.ui;

import fr.xgouchet.webmonitor.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class TargetFragment extends DialogFragment {
    
    private OnDismissListener mOnDismissListener;
    
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(R.string.title_target_new);
        
        return builder.create();
    }
    
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }
    
    
    public void setOnDismissListener(final OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
}
