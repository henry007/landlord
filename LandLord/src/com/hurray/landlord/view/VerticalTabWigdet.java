
package com.hurray.landlord.view;

import com.hurray.landlord.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabWidget;

public class VerticalTabWigdet extends TabWidget {
    Resources res;

    public VerticalTabWigdet(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        res = context.getResources();
        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    public void addView(View child) {
        
        int widgetWidth = (int) getResources().getDimension(R.dimen.tabhost_widget_individual_width);
        
        int widgetHeight = (int) getResources().getDimension(R.dimen.tabhost_widget_individual_height);

        LinearLayout.LayoutParams lp = new LayoutParams(
                widgetWidth, widgetHeight, 1.0f);
        lp.setMargins(0, 2, 0, 2);
        lp.gravity = Gravity.CENTER;
        child.setLayoutParams(lp);
        super.addView(child);
        child.setBackgroundDrawable(res.getDrawable(R.drawable.tab_selector));
    }

}
