package tk.foodpedia.android.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CameraPreview extends SurfaceView {
    private static final double ASPECT_TOLERANCE = 0.1;
    private boolean surfaceCreated;
    private int previewWidth;
    private int previewHeight;

    /**
     * Since preview surface is processed in other thread than a Camera,
     * this lock is needed not to pass null into Camera.setPreviewDisplay(surfaceHolder)
     */
    private ReentrantLock previewLock = new ReentrantLock();


    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    /**
     * Used to release preview lock thus allowing scanner thread to start preview
     */
    private void init() {
        getHolder().addCallback(new SurfaceHolder.Callback() {

            /**
             * Check for locking is needed since the surface is recreated when onStop() -> onStart()
             * thus calling this callback and trying to release an unlocked lock
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (isLocked()) unlock();
                surfaceCreated = true;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { /* */ }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                surfaceCreated = false;
            }
        });
    }


    /**
     * Calculates and stores size of the SurfaceView in pixels.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        previewWidth = previewHeight = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        setMeasuredDimension(previewWidth, previewHeight);
    }


    public boolean isSurfaceCreated() {
        return surfaceCreated;
    }


    public boolean isLocked() {
        return previewLock.isLocked();
    }


    public void lock() {
        previewLock.lock();
    }


    public void unlock() {
        previewLock.unlock();
    }


    /**
     * Selects best preview size supported by the Camera
     * based on the size and aspect ratio of the SurfaceView.
     *
     * @param sizes supported preview sizes
     * @return optimal preview size
     */
    @SuppressWarnings("deprecation")
    public Camera.Size getPreviewSize(List<Camera.Size> sizes) {
        Camera.Size previewSize = null;
        double targetRatio = (double) previewWidth / previewHeight;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - previewHeight) < minDiff) {
                previewSize = size;
                minDiff = Math.abs(size.height - previewHeight);
            }
        }

        if (previewSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - previewHeight) < minDiff) {
                    previewSize = size;
                    minDiff = Math.abs(size.height - previewHeight);
                }
            }
        }

        return previewSize;
    }
}
