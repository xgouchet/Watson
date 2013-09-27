package fr.xgouchet.webmonitor.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.common.Constants;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.data.TargetDAO;
import fr.xgouchet.webmonitor.service.UpdateService;
import fr.xgouchet.webmonitor.ui.notification.TargetNotification;


public class TargetFragment extends DialogFragment implements OnShowListener {
    
    private OnDismissListener mOnDismissListener;
    
    
    public void setOnDismissListener(final OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
    
    private int mAction;
    private long mTargetId;
    private String mTargetUrl;
    private Target mTarget;
    
    private EditText mUrl, mTitle;
    private Spinner mFrequency, mDifference;
    private AlertDialog mAlertDialog;
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Fragment Lifecycle
    //////////////////////////////////////////////////////////////////////////////////////
    
    
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // read arguments 
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        
        mAction = args.getInt(Constants.EXTRA_COMMAND);
        mTarget = args.getParcelable(Constants.EXTRA_TARGET);
    }
    
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
        setupFrequency();
        setupDifference();
        
        // finalize dialog 
        mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(this);
        
        return mAlertDialog;
    }
    
    
    
    @Override
    public void onResume() {
        super.onResume();
        
        if (mTarget == null) {
            mTitle.setText(R.string.title_target_new);
            mFrequency.setSelection(getFrequencyIndex(Settings.sDefaultFrequency));
            mDifference.setSelection(getDifferenceIndex(Settings.sDefaultDifference));
        } else {
            mTargetId = mTarget.getTargetId();
            mTargetUrl = mTarget.getUrl();
            mUrl.setText(mTargetUrl);
            mTitle.setText(mTarget.getTitle());
            mFrequency.setSelection(getFrequencyIndex(mTarget.getFrequency()));
            mDifference.setSelection(getDifferenceIndex(mTarget.getMinimumDifference()));
        }
    }
    
    
    
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // OnShowListener Implementation
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void onShow(final DialogInterface dialog) {
        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
                new OnClickListener() {
                    
                    @Override
                    public void onClick(final View v) {
                        onSaveAction();
                    }
                });
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
        // clear notifications
        if (TextUtils.isEmpty(mTargetUrl)) {
            TargetNotification.clearAllNotifications(getActivity(), mTargetUrl);
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
        
        TargetDAO dao = TargetDAO.getInstance(getActivity());
        
        switch (mAction) {
            case Constants.CMD_CREATE:
                dao.insertTarget(target);
                break;
            case Constants.CMD_EDIT:
                if (target.getTargetId() == 0) {
                    dao.insertTarget(target);
                } else {
                    dao.updateTarget(target);
                }
                break;
            default:
                Log.w("Watson", "Unknwon action " + mAction);
                break;
        }
        
        Intent update = new Intent(getActivity(), UpdateService.class);
        update.putExtra(Constants.EXTRA_TARGET, target);
        update.setAction(Constants.ACTION_CHECK_TARGET);
        getActivity().startService(update);
        
        dismiss();
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Spinner Utils
    //////////////////////////////////////////////////////////////////////////////////////
    
    private void setupFrequency() {
        SpinnerAdapter adapter;
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.frequency_names,
                android.R.layout.simple_spinner_dropdown_item);
        mFrequency.setAdapter(adapter);
    }
    
    private void setupDifference() {
        SpinnerAdapter adapter;
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.difference_names,
                android.R.layout.simple_spinner_dropdown_item);
        mDifference.setAdapter(adapter);
    }
    
    private long getSelectedFrequency() {
        int pos = mFrequency.getSelectedItemPosition();
        return Constants.FREQUENCIES[pos];
    }
    
    private int getSelectedDifference() {
        int pos = mDifference.getSelectedItemPosition();
        return Constants.DIFFERENCES[pos];
    }
    
    private int getFrequencyIndex(final long frequency) {
        int index;
        // TODO check this algo
        if (frequency == 0) {
            index = Settings.sDefaultFrequency;
        } else {
            index = -1;
            for (int i = 0; i < Constants.FREQUENCIES.length; i++) {
                if (Constants.FREQUENCIES[i] >= frequency) {
                    index = i;
                    break;
                }
            }
            
            if (index == -1) {
                index = Constants.FREQUENCIES.length - 1;
            }
        }
        
        return index;
    }
    
    private int getDifferenceIndex(final int difference) {
        int index;
        // TODO check 
        index = -1;
        for (int i = 0; i < Constants.DIFFERENCES.length; i++) {
            if (Constants.DIFFERENCES[i] >= difference) {
                index = i;
                break;
            }
        }
        
        if (index == -1) {
            index = Constants.DIFFERENCES.length - 1;
        }
        
        return index;
    }
}
