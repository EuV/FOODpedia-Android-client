package tk.foodpedia.android.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Downloadable implements Serializable {

    public void fill(ViewGroup rootView) {
        for (View view : getAllChildren(rootView)) {
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
                String text = (String) method.invoke(this);
                ((TextView) view).setText(text);
                view.setVisibility(text == null ? View.GONE : View.VISIBLE);
            } catch (Exception e) { /* */ }
        }
    }

    private ArrayList<View> getAllChildren(View view) {
        ArrayList<View> children = new ArrayList<>();

        children.add(view);

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                children.addAll(getAllChildren(child));
            }
        }

        return children;
    }
}
