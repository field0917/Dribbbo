package com.jiuzhang.yeyuan.dribbbo.custom_class;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.WrappedDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jiuzhang.yeyuan.dribbbo.R;


public class ClearableEditText extends AppCompatEditText implements View.OnTouchListener,
                                                                    View.OnFocusChangeListener,
                                                                    TextWatcher {
    private Drawable mClearTextIcon;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;
    public ClearableEditText(Context context) {
        super(context);
        init(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init (final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_highlight_off_black_18dp);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorSecondaryText));
        mClearTextIcon = drawable;
        mClearTextIcon.setBounds(0,0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicWidth());
        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    private void setClearIconVisible (boolean visible) {
        mClearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearTextIcon : null,
                compoundDrawables[3]);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (isFocused()) {
            setClearIconVisible(charSequence.length() > 0);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        if (mClearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setText("");
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        this.mOnFocusChangeListener = l;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.mOnTouchListener = l;
    }
}
