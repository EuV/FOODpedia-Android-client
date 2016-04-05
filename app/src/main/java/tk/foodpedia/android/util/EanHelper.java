package tk.foodpedia.android.util;

public final class EanHelper {
    private static final char INTERNAL_USE_BARCODE_PREFIX = '2';
    private static final int EAN_8_LENGTH = 8;
    private static final int EAN_13_LENGTH = 13;

    private EanHelper() { /* */ }

    public static boolean isValid(String ean) {
        if (ean == null) return false;
        int length = ean.length();
        return length == EAN_8_LENGTH || length == EAN_13_LENGTH;
    }

    public static boolean isReserved(String ean) {
        return ean != null && ean.charAt(0) == INTERNAL_USE_BARCODE_PREFIX;
    }
}
