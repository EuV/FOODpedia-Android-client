package tk.foodpedia.android.util;

import java.io.InputStream;
import java.util.Scanner;

public final class StringHelper {
    private StringHelper() { /* */ }

    public static String fromStream(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
