package com.xiaomi.supercleanmaster.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;


public class ProgressDialogFragment extends DialogFragment {
    int mIndeterminateDrawble;
    String mMessage;
    static View mContentView;

    public static ProgressDialogFragment newInstance(int indeterminateDrawble, String message) {
        ProgressDialogFragment f = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putInt("indeterminateDrawble", indeterminateDrawble);
        args.putString("message", message);
        f.setArguments(args);
        return f;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        mIndeterminateDrawble = getArguments().getInt("indeterminateDrawble");
        mMessage = getArguments().getString("message");
        ProgressDialog mProcessDialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        if (mIndeterminateDrawble > 0) {
            mProcessDialog.setIndeterminateDrawable(getActivity().getResources().getDrawable(mIndeterminateDrawble));
        }
        if (mMessage != null) {
            mProcessDialog.setMessage(mMessage);
        }
        return mProcessDialog;
    }

    public void setMessage(String mMessage) {
        if (mMessage != null) {
            setMessage(mMessage);
        }
    }
}
