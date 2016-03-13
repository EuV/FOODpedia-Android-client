package tk.foodpedia.android.util;

import android.support.annotation.StringRes;

import java.io.InputStream;
import java.util.Scanner;

import tk.foodpedia.android.App;

public final class StringHelper {
    private StringHelper() { /* */ }

    public static String fromStream(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public static String format(@StringRes int strId, Object... formatArgs) {
        return App.getContext().getString(strId, formatArgs);
    }
}
