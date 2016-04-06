package tk.foodpedia.android.util;


import tk.foodpedia.android.App;

public final class Converter {
    private Converter() { /* */ }

    public static float pxToDp(float px) {
        return px / density();
    }

    public static float dpToPx(float dp) {
        return dp * density();
    }

    private static float density() {
        if (App.getContext() == null) {
            return 1; // Basically, this is for IDE layout preview
        } else {
            return App.getContext().getResources().getDisplayMetrics().density;
        }
    }
}
