package io.keepcube.kcapp.Data;

import java.util.ArrayList;
import java.util.Collections;

import io.paperdb.Paper;

/**
 * Created by ondrej on 9.8.17.
 */

public class Dashboard {
    private static ArrayList<Integer> roomIndexes = null;
    private static ArrayList<Integer> deviceIndexes = null; // TODO: 11.8.17 check i u ostatnich

    private static OnDeviceChangedListener deviceChangedListener = null;


    private static int lastUnregisteredPosition = -1;
    private static int lastRoomIndex = -1;
    private static int lastDeviceIndex = -1;

    public static void load() {
        roomIndexes = Paper.book().read(Key.DASHBOARD_ROOM_INDEXES, new ArrayList<Integer>());
        deviceIndexes = Paper.book().read(Key.DASHBOARD_DEVICE_INDEXES, new ArrayList<Integer>());


    }


    public static void save() {
        Paper.book().write(Key.DASHBOARD_ROOM_INDEXES, roomIndexes);
        Paper.book().write(Key.DASHBOARD_DEVICE_INDEXES, deviceIndexes);
    }


    public static void registerDevice(int roomIndex, int deviceIndex) {
        roomIndexes.add(roomIndex);
        deviceIndexes.add(deviceIndex);


        if (deviceChangedListener != null) {
            deviceChangedListener.onDeviceAdded(roomIndexes.size() - 1);
        }


    }

    public static Device unregisterDevice(int position) {
        lastRoomIndex = roomIndexes.get(position);
        lastDeviceIndex = deviceIndexes.get(position);
        Device old = Home.room(roomIndexes.get(position)).device(deviceIndexes.get(position));
        roomIndexes.remove(position);
        deviceIndexes.remove(position);

        lastUnregisteredPosition = position;

        if (deviceChangedListener != null) {
            deviceChangedListener.onDeviceRemoved(position);
        }

        return old;
        // TODO: 10.8.17 listener
    }

    public static Device getDevice(int position) {
        return Home.room(roomIndexes.get(position)).device(deviceIndexes.get(position));
    }


    public static void restoreLastUnregistered() {


        roomIndexes.add(lastUnregisteredPosition, lastRoomIndex);
        deviceIndexes.add(lastUnregisteredPosition, lastDeviceIndex);

        if (deviceChangedListener != null) {
            deviceChangedListener.onDeviceAdded(lastUnregisteredPosition);
        }
    }



//    public class Led {
//
//
//        public class Keyframes {
//            private  ArrayList<Keyframe> keyframes = new ArrayList<>();
//
//            public void add(Keyframe keyframe) {
//                keyframes.add(keyframe);
//            }
//
//            public Keyframe get(int position) {
//                return keyframes.get(position);
//            }
//
//            public void remove(int position) {
//
//            }
//
//            public void swap(int fromPosition, int toPosition) {
//
//
//
//            }
//
//
//        }
//
//    }


    public static void swap(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(roomIndexes, i, i + 1);
                Collections.swap(deviceIndexes, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(roomIndexes, i, i - 1);
                Collections.swap(deviceIndexes, i, i - 1);
            }
        }
    }


    public static int numberOfDevices() {
        return roomIndexes.size();
    }


    public static Room getParentRoom(int position) {

        return Home.room(roomIndexes.get(position));

    }

    // Rooms Listener interface
    public static void setOnDeviceChangedListener(OnDeviceChangedListener listener) {
        deviceChangedListener = listener;
    }

    public interface OnDeviceChangedListener {
        void onDeviceAdded(int position);

        void onDeviceRemoved(int position);
    }


}
