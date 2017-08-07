package io.keepcube.kcapp.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class Home {
    private static String TAG = "Home";
    private static boolean dataLoaded = false;

    // Listeners
    private static OnRoomChangedListener roomChangedListener = null;

    // Rooms
    private static ArrayList<Room> rooms = new ArrayList<>();
//    private static ArrayList<String> roomNames = new ArrayList<>();
//    private static ArrayList<String> roomDescriptions = new ArrayList<>();

    // Users
    // private static ArrayList<User> users = new ArrayList<>();


    public static void load(Context context) {
        Paper.init(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dataLoaded) save(); // Re-load detection
                dataLoaded = true;

                rooms = Paper.book().read(Key.ROOMS, new ArrayList<Room>());

//                roomNames = Paper.book().read(Key.ROOMS_NAMES, new ArrayList<String>());
//                roomDescriptions = Paper.book().read(Key.ROOMS_DESCRIPTIONS, new ArrayList<String>());
            }
        }).start();
    }

    public static void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long bench = System.currentTimeMillis();
                Paper.book().write(Key.ROOMS, rooms);
//                Paper.book().write(Key.ROOMS_NAMES, roomNames);
//                Paper.book().write(Key.ROOMS_DESCRIPTIONS, roomDescriptions);
                Log.d(TAG, "Saved! Took " + String.valueOf(System.currentTimeMillis() - bench) + "ms");
            }
        }).start();
    }

    public static void fetch() {
        // TODO: 4.8.17 Chtít po Narkovi, aby udělal endpoint, ze kterého se appka dozví zda se na serveru něco změnilo či ne.
        // TODO: 4.8.17 Nebo spíš endpoint, který vrátí SHA1 checksum celé databáze (devices, rooms, users...)
        // TODO: 4.8.17 Pokud se databázový checksum neliší s tím minulým, nic se nestane a jen se načtou data z interní paměti.
        // TODO: 4.8.17 Pokud se databáze změní externě (checksum se liší), vyhodí se dialog s načítáním a stáhnou se data.
    }

    public static void autoSave(int periodSeconds) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (dataLoaded) {
                    save();
                    Log.i(TAG, "Autosaved!");
                }
            }
        }, 0, periodSeconds * 1000);
    }


    // Rooms
    public static void addRoom(@NonNull String name, @Nullable String description) {
        /*
         * TODO: 3.8.17 REST TEMPLATE:
         * Api.asyncRequest("/v1/room/add", Method.POST)
         *      .onError(
         *          // don't add anything anywhere
         *          Toast("Failed to connect to KeepCube. Please check connection and try again");
         *      )
         *      .onSuccess(
         *          rooms.add(name, decsription);
         *          ...
         *          roomChangedListener.onRoomAdded(rooms.size(), name, description); // Fire the listener
         *      );
         */

//        roomNames.add(name);
//        roomDescriptions.add(description);
        rooms.add(new Room(name, description));

        if (roomChangedListener != null) {
            roomChangedListener.onRoomAdded(rooms.size() - 1, name, description);
        }
    }


    public static void removeRoom(int index) {
//        roomNames.remove(index);
//        roomDescriptions.remove(index);
        Room removed = rooms.remove(index);
        if (roomChangedListener != null) {
            roomChangedListener.onRoomRemoved(index, removed.name, removed.description);
        }

    }

    public static boolean hasRoom(String name) {
        for (int i = 0; i < rooms.size(); i++) if (rooms.get(i).name.equals(name)) return true;
        return false;
    }

    public static int numberOfRooms() {
        return rooms.size();
    }


    //


    //


    //


    //


    //


    @SuppressWarnings("SuspiciousMethodCalls")
    public static Room room(String name) {
        return rooms.get(rooms.indexOf(name));
    }

    public static Room room(int index) {
        return rooms.get(index);
    }


    //


    //


    //


    public static void setOnRoomChangedListener(OnRoomChangedListener listener) {
        roomChangedListener = listener;
    }


    public interface OnRoomChangedListener {
        void onRoomAdded(int position, @NonNull String name, @Nullable String description);

        void onRoomRemoved(int position, @NonNull String name, @Nullable String description);
    }

    public static class Cube {
        private static String ip = "";

        public static void setIP(String ip) {
            Cube.ip = ip;
        }

    }
}
