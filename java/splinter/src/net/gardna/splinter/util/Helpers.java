package net.gardna.splinter.util;

public class Helpers {

    public static byte[] SliceArray(byte[] array, int start) {
        return SliceArray(array, start, array.length);
    }

    public static byte[] SliceArray(byte[] array, int start, int end) {
        byte[] out = new byte[end - start];

        for (int i = start; i < end; i++) {
            out[i - start] = array[i];
        }

        return out;
    }
}
