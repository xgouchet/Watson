package fr.xgouchet.webmonitor.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.data.Constants;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.data.TargetDAO;
import fr.xgouchet.webmonitor.receiver.BootReceiver;


public class TargetFragment extends DialogFragment {
    
    private OnDismissListener mOnDismissListener;
    
    
    public void setOnDismissListener(final OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
    
    private int mAction;
    private long mTargetId;
    private String mTargetUrl;
    
    private EditText mUrl, mTitle;
    private Spinner mFrequency, mDifference;
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Fragment Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(R.string.title_target_new);
        
        // Custom content 
        View content = getActivity().getLayoutInflater().inflate(R.layout.fragment_target, null);
        builder.setView(content);
        
        // Done / Discard buttons
        builder.setPositiveButton(R.string.ui_save, null);
        builder.setNegativeButton(R.string.ui_cancel, null);
        
        
        // Get widgets 
        mUrl = (EditText) content.findViewById(R.id.editUrl);
        mTitle = (EditText) content.findViewById(R.id.editTitle);
        mFrequency = (Spinner) content.findViewById(R.id.spinnerFrequency);
        mDifference = (Spinner) content.findViewById(R.id.spinnerDifference);
        
        // Setup spinners
        SpinnerAdapter adapter;
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.frequency_names,
                android.R.layout.simple_spinner_dropdown_item);
        mFrequency.setAdapter(adapter);
        
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.difference_names,
                android.R.layout.simple_spinner_dropdown_item);
        mDifference.setAdapter(adapter);
        
        
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            
            @Override
            public void onShow(final DialogInterface dialog) {
                
                Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(final View view) {
                        onSaveAction();
                    }
                });
            }
        });
        
        return alertDialog;
    }
    
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Dialog Callbacks
    //////////////////////////////////////////////////////////////////////////////////////
    
    
    private void onSaveAction() {
        boolean urlValid = URLUtil.isValidUrl(mUrl.getText().toString());
        
        if (!urlValid) {
            mUrl.setError("This url is not valid");
            mUrl.requestFocus();
        } else if (TextUtils.isEmpty(mTitle.getText().toString().trim())) {
            mTitle.setError("The title must not be empty");
            mTitle.requestFocus();
        } else {
            onSaveTarget();
        }
    }
    
    private void onSaveTarget() {
        if (!TextUtils.isEmpty(mTargetUrl)) {
            // TODO WatsonNotification.clearAllNotifications(this, mTargetUrl);
        }
        
        Target target = new Target();
        
        target.setUrl(mUrl.getText().toString());
        target.setTitle(mTitle.getText().toString());
        target.setTargetId(mTargetId);
        
        target.setFrequency(getSelectedFrequency());
        target.setMinimumDifference(getSelectedDifference());
        
        if (!target.getUrl().equals(mTargetUrl)) {
            target.setLastUpdate(0);
            target.setLastCheck(0);
            target.setStatus(Status.UNKNOWN);
        }
        
        TargetDAO dao = TargetDAO.getSingleton();
        dao.init(getActivity());
        switch (mAction) {
            case Constants.ACTION_CREATE:
                dao.insertTarget(target);
                break;
            case Constants.ACTION_EDIT:
                dao.updateTarget(target);
                break;
            default:
                Log.w("Watson", "Unknwon action " + mAction);
                break;
        }
        
        Intent update = new Intent(getActivity(), BootReceiver.class);
        getActivity().sendBroadcast(update);
        
        dismiss();
    }
    //////////////////////////////////////////////////////////////////////////////////////
    // Spinner Utils
    //////////////////////////////////////////////////////////////////////////////////////
    
    private long getSelectedFrequency() {
        int pos = mFrequency.getSelectedItemPosition();
        return Constants.FREQUENCIES[pos];
    }
    
    private int getSelectedDifference() {
        int pos = mDifference.getSelectedItemPosition();
        return Constants.DIFFERENCES[pos];
    }
    
}
