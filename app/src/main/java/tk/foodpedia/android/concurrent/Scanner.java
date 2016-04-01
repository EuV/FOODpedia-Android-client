package tk.foodpedia.android.concurrent;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;

import java.io.IOException;

import tk.foodpedia.android.App;
import tk.foodpedia.android.R;
import tk.foodpedia.android.util.ToastHelper;
import tk.foodpedia.android.view.CameraPreview;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

@SuppressWarnings("deprecation")
public final class Scanner extends HandlerThread {
    private static Scanner scannerInstance;

    private Handler scannerThreadHandler;
    private ScannerCallbacks callbacks;
    private CameraPreview cameraPreview;
    private Camera camera;

    public interface ScannerCallbacks {
        void onScanCompleted(String barcode);
    }


    private Scanner() {
        super("ScannerThread");
        start();
        scannerThreadHandler = new Handler(getLooper());
    }


    public static Scanner getInstance(ScannerCallbacks scannerCallbacks, CameraPreview cameraPreview) {
        if (scannerInstance == null) {
            scannerInstance = new Scanner();
            System.loadLibrary("iconv");
        }

        scannerInstance.callbacks = scannerCallbacks;
        scannerInstance.cameraPreview = cameraPreview;

        return scannerInstance;
    }


    public void startScan() {

        // Preview surface may already be created in case of onPause() -> onResume() (no onStop())
        if (!cameraPreview.isSurfaceCreated()) {

            // Seize preview lock on the UI thread thus blocking ScannerThread
            cameraPreview.lock();
        }

        // Must be called AFTER preview lock is set by UI thread!
        scannerThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                startScanOnSeparateThread();
            }
        });
    }


    public void stopScan() {
        scannerThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                stopScanOnSeparateThread();
            }
        });
    }


    private void startScanOnSeparateThread() {
        try {
            camera = Camera.open(0);
        } catch (RuntimeException e) {
            ToastHelper.show(R.string.error_failed_to_open_camera);
            return;
        }

        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = cameraPreview.getPreviewSize(parameters.getSupportedPreviewSizes());
        parameters.setPreviewSize(size.width, size.height);
        parameters.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);

        int imageFormat = parameters.getPreviewFormat();
        int bufferSize = size.width * size.height * ImageFormat.getBitsPerPixel(imageFormat) / 8;
        camera.addCallbackBuffer(new byte[bufferSize]);
        camera.setPreviewCallbackWithBuffer(new CameraPreviewCallback(size.width, size.height));
        camera.setDisplayOrientation(90);

        // Wait here until UI thread creates surface for camera's preview and releases the lock
        cameraPreview.lock();
        try {
            camera.setPreviewDisplay(cameraPreview.getHolder());
        } catch (IOException e) {
            ToastHelper.show(R.string.error_failed_to_start_preview);
        } finally {
            cameraPreview.unlock();
        }

        // Sanity check, as it may already be destroyed
        if (cameraPreview.isShown()) {
            camera.startPreview();
        }
    }


    private void stopScanOnSeparateThread() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


    private class CameraPreviewCallback implements Camera.PreviewCallback {
        private static final char INTERNAL_USE_BARCODE_PREFIX = '2';
        private static final int EAN_8_LENGTH = 8;
        private static final int EAN_13_LENGTH = 13;

        private ImageScanner imageScanner;
        private Image previewFrame;

        CameraPreviewCallback(int previewWidth, int previewHeight) {
            previewFrame = new Image(previewWidth, previewHeight, "Y800");
            imageScanner = new ImageScanner();
            imageScanner.setConfig(0, Config.X_DENSITY, 3);
            imageScanner.setConfig(0, Config.Y_DENSITY, 3);
        }


        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            previewFrame.setData(data);

            if (imageScanner.scanImage(previewFrame) == 0) {
                camera.addCallbackBuffer(data);
                return;
            }

            for (Symbol symbol : imageScanner.getResults()) {
                final String barcode = symbol.getData();
                if (validateBarcode(barcode)) {
                    camera.stopPreview();
                    deliverResult(barcode);
                }
            }

            camera.addCallbackBuffer(data);
        }


        private boolean validateBarcode(String barcode) {
            if (barcode == null) return false;

            int length = barcode.length();
            if (!(length == EAN_8_LENGTH || length == EAN_13_LENGTH)) return false;

            if (barcode.charAt(0) == INTERNAL_USE_BARCODE_PREFIX) {
                ToastHelper.showOnce(R.string.error_barcode_for_internal_use);
                return false;
            }

            return true;
        }


        private void deliverResult(final String barcode) {
            App.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callbacks.onScanCompleted(barcode);
                }
            });
        }
    }
}
