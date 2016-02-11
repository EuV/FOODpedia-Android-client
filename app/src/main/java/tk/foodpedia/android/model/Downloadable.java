package tk.foodpedia.android.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.Method;

public abstract class Downloadable implements Serializable {
    private volatile boolean downloaded = false;

    public abstract int getQueryId();

    public abstract String[] getQueryParams();

    public void downloaded() {
        downloaded = true;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void fill(ViewGroup rootView) {
        for (int i = 0; i < rootView.getChildCount(); i++) {

            View view = rootView.getChildAt(i);
            if (!(view instanceof TextView)) continue;

            String methodName = (String) view.getTag();
            if (methodName == null) continue;

            Method method;
            try {
                method = getClass().getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) {
                continue;
            }

            try {
                ((TextView) view).setText((String) method.invoke(this));
            } catch (Exception e) { /* */ }
        }
    }
}
