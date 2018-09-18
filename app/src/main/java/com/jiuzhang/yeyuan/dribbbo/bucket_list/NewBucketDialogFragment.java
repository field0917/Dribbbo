package com.jiuzhang.yeyuan.dribbbo.bucket_list;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jiuzhang.yeyuan.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class NewBucketDialogFragment extends DialogFragment {

    public static final String TAG = "fragment_new_bucket";
    public static final String KEY_NEW_BUCKET_NAME = "bucket name";
    public static final String KEY_NEW_BUCKET_DESCRIPTION = "bucket description";

    @BindView (R.id.new_bucket_name) EditText newBucketName;
    @BindView (R.id.new_bucket_description) EditText newBucketDescription;

    public NewBucketDialogFragment() {
        // Required empty public constructor
    }

    public static NewBucketDialogFragment newInstance() {
        NewBucketDialogFragment fragment = new NewBucketDialogFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_new_bucket_dialog, null);
        ButterKnife.bind(this, view);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.new_bucket)
                .setPositiveButton(R.string.new_bucket_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent result = new Intent();
                        result.putExtra(KEY_NEW_BUCKET_NAME, newBucketName.getText().toString());
                        result.putExtra(KEY_NEW_BUCKET_DESCRIPTION, newBucketDescription.getText().toString());
                        getTargetFragment().onActivityResult(BucketListFragment.REQ_NEW_BUCKET, RESULT_OK, result);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.new_bucket_cancel, null)
                .create();
    }

}
