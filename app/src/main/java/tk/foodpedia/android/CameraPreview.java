package tk.foodpedia.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView {

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int side = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        setMeasuredDimension(side, side);
    }
}
