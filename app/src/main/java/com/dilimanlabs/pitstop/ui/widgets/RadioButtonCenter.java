package com.dilimanlabs.pitstop.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

import com.dilimanlabs.pitstop.R;

public class RadioButtonCenter extends RadioButton {

    public RadioButtonCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, 0, 0);
        buttonDrawable = a.getDrawable(1);
        setButtonDrawable(android.R.color.transparent);
    }
    Drawable buttonDrawable;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (buttonDrawable != null) {
            buttonDrawable.setState(getDrawableState());
            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            final int height = buttonDrawable.getIntrinsicHeight();

            int y = 0;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y = getHeight() - height;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y = (getHeight() - height) / 2;
                    break;
            }

            int buttonWidth = buttonDrawable.getIntrinsicWidth();
            int buttonLeft = (getWidth() - buttonWidth) / 2;
            buttonDrawable.setBounds(buttonLeft, y, buttonLeft+buttonWidth, y + height);
            buttonDrawable.draw(canvas);
        }
    }
}
