package io.keepcube.kcapp.Data;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ondrej on 1.8.17.
 */

public class Data {


    public static void fetch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO: 1.8.17 get remote data
            }
        }).start();
    }


    public static class Dashboard {
        ArrayList<Item> items = new ArrayList<>();


        public static class Item {
            String name = "";
            String room = "";
            int type = 0;

            // LED
            GradientDrawable gradient;
            int color = 0;


            // as a LED
            public Item(String name, String room, GradientDrawable gradient, int color) {
                this.type = Type.LED;
                this.name = name;
                this.room = room;

                this.gradient = gradient;
                this.color = color;


            }

        }

    }




    public static  class Accessories{

        public static final List<Fragment> fragList = new ArrayList<>();
        public static final List<String> fragTitleList = new ArrayList<>();


    }



}