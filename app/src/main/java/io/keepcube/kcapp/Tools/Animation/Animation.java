package io.keepcube.kcapp.Tools.Animation;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by ondrej on 9.8.17.
 */

public class Animation {
    public boolean loop = false;
    private ArrayList<Keyframe> keyframes = new ArrayList<>();

    public boolean add(Keyframe keyframe) {
        return keyframes.add(keyframe);
    }

    public Keyframe get(int position) {
        return keyframes.get(position);
    }

    public Keyframe remove(int position) {
        return keyframes.remove(position);
    }

    public int numberOfKeyframes() {
        return keyframes.size();
    }

    public void swap(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(keyframes, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(keyframes, i, i - 1);
            }
        }
    }

    public GradientDrawable generateMultigradient(Context context) {
        ArrayList<Integer> colorList = new ArrayList<>();

        for (int i = 0; i < keyframes.size(); i++)
            if (keyframes.get(i).type == Keyframe.TYPE_COLOR)
                for (int j = 0; j < 3; j++)
                    colorList.add(keyframes.get(i).color);

        if (colorList.size() < 2)
            colorList.add(colorList.get(0));

        Iterator<Integer> iterator = colorList.iterator();
        int[] colors = new int[colorList.size()];
        for (int i = 0; i < colors.length; i++) colors[i] = iterator.next();

        GradientDrawable multigradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        multigradient.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));

        return multigradient;
    }

}
