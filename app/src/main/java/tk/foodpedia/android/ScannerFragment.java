package tk.foodpedia.android;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class ScannerFragment extends Fragment {
    private OnScanCompletedListener onScanCompletedListener;
    private Camera camera;
    private ImageScanner scanner;
    private Image codeImage;

    public interface OnScanCompletedListener {
        void onScanCompleted(String barcode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onScanCompletedListener = (OnScanCompletedListener) context;
    }


    // TODO: Total infernal refactoring!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_scanner, container, false);

        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        ((CameraPreview) v.findViewById(R.id.camera_preview)).getHolder().addCallback(new Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    die(R.string.error_failed_to_start_preview);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (camera == null) return;

                Parameters parameters = camera.getParameters();
                Size size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(size.width, size.height);
                camera.setParameters(parameters);

                codeImage = new Image(size.width, size.height, "Y800");

                int imageFormat = parameters.getPreviewFormat();
                int bufferSize = size.width * size.height * ImageFormat.getBitsPerPixel(imageFormat) / 8;
                camera.addCallbackBuffer(new byte[bufferSize]);
                camera.setPreviewCallbackWithBuffer(new PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        codeImage.setData(data);
                        int result = scanner.scanImage(codeImage);
                        if (result != 0) {
                            SymbolSet syms = scanner.getResults();
                            for (Symbol sym : syms) {
                                String lastScannedCode = sym.getData();
                                if (lastScannedCode != null) {
                                    onScanCompletedListener.onScanCompleted(lastScannedCode);
                                    camera.stopPreview();
                                }
                            }
                        }
                        camera.addCallbackBuffer(data);
                    }
                });

                camera.setDisplayOrientation(90);
                camera.startPreview();
                camera.autoFocus(null);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.stopPreview();
                }
            }
        });

        v.findViewById(R.id.button_find_product).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setOnClickListener(null);
                String barcode = ((EditText) v.findViewById(R.id.edit_text_barcode)).getText().toString();
                onScanCompletedListener.onScanCompleted(barcode);
            }
        });

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            // TODO: move to separate thread
            camera = Camera.open(0);
        } catch (Exception e) {
            die(R.string.error_failed_to_open_camera);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    protected Size getOptimalPreviewSize(List<Size> sizes, int targetWidth, int targetHeight) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) targetWidth / targetHeight;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }


    protected void die(@StringRes int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }
}
