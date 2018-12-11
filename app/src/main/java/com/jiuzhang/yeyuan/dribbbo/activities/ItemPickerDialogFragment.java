package com.jiuzhang.yeyuan.dribbbo.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ItemPickerDialogFragment extends DialogFragment {

    /**
     * Interface for notification of item selection
     *
     * If the owning activity implement this interface, then the fragment will invoke
     * its onItemSelected() when user click OK button
     */
    public interface OnItemSelectedListener {
        void onItemSelected(ItemPickerDialogFragment fragment, String item, int index);
    }
    public static final String TAG = "item_picker_dialog_fragment";
    private static final String ARG_TITLE = "title";
    private static final String ARG_ITEMS = "items";
    private static final String ARG_SELECTED_INDEX = "selected_index";

    private String title;
    private String[] items;
    private int selectedIndex;

    /**
     * Constructor
     */
    public ItemPickerDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of ItemPickerDialogFragment with specific arguments
     * @param title Dialog title text
     * @param items Selectable items texts
     * @param selectedIndex Initial selected index, or -1 if no item should be pre-selected
     * @return ItemPickerDialogFragment
     */
    public static ItemPickerDialogFragment newInstance(String title, String[] items, int selectedIndex) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putStringArray(ARG_ITEMS, items);
        args.putInt(ARG_SELECTED_INDEX, selectedIndex);
        ItemPickerDialogFragment fragment = new ItemPickerDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_INDEX, selectedIndex);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(ARG_SELECTED_INDEX);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARG_TITLE);
            items = args.getStringArray(ARG_ITEMS);
            selectedIndex = args.getInt(ARG_SELECTED_INDEX, 2);
        }

        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(ARG_SELECTED_INDEX, selectedIndex);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Test", "OK is pressed!");
                        Activity activity = getActivity();
                        if (activity instanceof OnItemSelectedListener) {
                            if (selectedIndex >= 0 && selectedIndex < items.length) {
                                String item = items[selectedIndex];
                                OnItemSelectedListener listener = (OnItemSelectedListener) activity;
                                listener.onItemSelected(ItemPickerDialogFragment.this, item, selectedIndex);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Test", "Cancel clicked!");
                    }
                })
                .setSingleChoiceItems(items, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Test", "user clicked item with index" + i);
                        selectedIndex = i;
                    }
                })
                .create();

    }
}
