package io.keepcube.kcapp.Data;

import android.content.Context;
import android.support.annotation.NonNull;

import io.keepcube.kcapp.R;
import io.keepcube.kcapp.Tools.Animation.Animation;

/**
 * Created by ondrej on 4.8.17.
 */

public class Device {
    private int type = 0;
    private String name = null;

    private Device(String name, int type) {
        this.name = name;
        this.type = type;
    }

    // No setters - only getters!
    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static class Dimmer extends Device {
        private int intenity = 0;

        public Dimmer(@NonNull String name) {
            super(name, Type.DIMMER);
        }


        public int getIntenity() {
            return intenity;
        }

        public void setIntenity(int intenity) {
            this.intenity = intenity;
        }

        public String getLabel(Context context) {
            return String.format(context.getString(R.string.intensity_format), intenity);
        }

    }


    public static class Led extends Device {
        public static final int CHARACTER_SOLID = 1;
        public static final int CHARACTER_ANIM = 2;
        private int character = CHARACTER_SOLID; // default
        private int solidColor = 0xffd6d7d7; // default button color
        private Animation animation = null;

        public Led(@NonNull String name) {
            super(name, Type.LED);
        }

        public int getSolidColor() {
            return solidColor;
        }

        public void setSolidColor(int color) {
            this.character = CHARACTER_SOLID;
            this.solidColor = color;
        }

        public Animation getAnimation() {
            return animation;
        }

        public void setAnimation(Animation animation) {
            this.character = CHARACTER_ANIM;
            this.animation = animation;
        }

        public String getLabel(Context context) {
            switch (this.character) {
                case CHARACTER_ANIM:
                    return context.getResources().getString(R.string.animation);

                case CHARACTER_SOLID:
                    return context.getResources().getString(R.string.color);

                default:
                    return "Not possible!";
            }
        }

        public int getCharacter() {
            return character;
        }
    }


    public static class Switch extends Device {
        // TODO: 9.8.17 variables

        public Switch(@NonNull String name) {
            super(name, Type.SWITCH);
        }
    }


//    public static class Custom extends Device {
//        // TODO: 9.8.17 variables
//
//        public Custom(@NonNull String name, int type) {
//            super(name, Type.custom()); // TODO: 9.8.17 ?????
//        }
//    }


}
