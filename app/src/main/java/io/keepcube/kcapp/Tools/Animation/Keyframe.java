package io.keepcube.kcapp.Tools.Animation;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by ondrej on 28.7.17.
 */

public class Keyframe {
    static final int TYPE_COLOR = 1;
    static final int TYPE_WAIT = 2;

    int color = 0;
    int time = 0;
    int type = 0;

    public Keyframe(int color, int time) {
        this.type = TYPE_COLOR;
        this.color = color;
        this.time = time;
    }

    public Keyframe(int time) {
        this.type = TYPE_WAIT;
        this.time = time;
    }

    public static int textColorFromBackground(@ColorInt int color) {
        if ((Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114) > 186) return Color.BLACK;
        else return Color.WHITE;
    }
}