package io.keepcube.kcapp.Data;

import io.paperdb.Paper;

/**
 * Created by ondrej on 20.8.17.
 */

public class FirstTime {

    /**
     * This method should be used as a detection of the first time call.
     *
     * @param key the Key
     * @return true if it was first prompted by a specific key, otherwise false
     */
    public static boolean is(String key) {
        if (Paper.book("FirstTime").read(key, true)) {
            Paper.book("FirstTime").write(key, false);
            return true;
        } else return false;
    }

    /**
     * @param key the Key
     * @return true if there was called commit() with the same key
     */
    public static boolean manual(String key) {
        return Paper.book("FirstTime").read(key, true);
    }

    /**
     * manual() with the same key will no longer be true.
     *
     * @param key the Key
     */
    public static void commit(String key) {
        Paper.book("FirstTime").write(key, false);
    }

    /**
     * Resets behavior of manual() and is() methods.
     *
     * @param key the Key
     */
    public static void reset(String key) {
        Paper.book("FirstTime").delete(key);
    }

}
