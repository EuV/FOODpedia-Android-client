package tk.foodpedia.android;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import tk.foodpedia.android.util.Converter;

/**
 * Hides FAB when user scrolls page down (ignoring jitter), shows it
 * when scrolling is upward or if content is scrolled to the top.
 */
public class HideOnScrollFabBehavior extends FloatingActionButton.Behavior {
    private static final float JITTER_TOLERANCE_DP = 3;
    private static final float JITTER_TOLERANCE_PX = Converter.dpToPx(JITTER_TOLERANCE_DP);

    public HideOnScrollFabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout layout, FloatingActionButton fab, View child, View target, int axes) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(layout, fab, child, target, axes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout layout, FloatingActionButton fab, View v, int dx, int dy, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(layout, fab, v, dx, dy, dxUnconsumed, dyUnconsumed);
        if (dy > JITTER_TOLERANCE_PX && fab.getVisibility() == View.VISIBLE) {
            fab.hide();
        } else if ((dy < -JITTER_TOLERANCE_PX || dyUnconsumed < 0) && fab.getVisibility() != View.VISIBLE) {
            fab.show();
        }
    }
}
