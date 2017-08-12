package io.keepcube.kcapp.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import io.keepcube.kcapp.Fragment.Tab.TabDeviceRoomFragment;

public class Room {
    public String name = null;
    public String description = null;

    private ArrayList<Device> devices = new ArrayList<>();

    private TabDeviceRoomFragment devicesFrag = null;


    public Room(@NonNull String name, @Nullable String description) {
        this.name = name;
        this.description = description;
    }

    public Device device(int index) {
        return devices.get(index);
    }

    @Deprecated
    public Device device(String name) {
        for (int i = 0; i < devices.size(); i++) if (devices.get(i).getName().equals(name)) return devices.get(i);
        return null;
    }


    public void updateDevice(int index, Device device) {
//        devices.add(device);
        devices.set(index, device);
    }

    public void addDevice(Device device) {
        devices.add(device);
    }

    public void removeDevice(int index) {
        Device old = devices.remove(index);
//        if (roomChangedListener != null) {
//            roomChangedListener.onRoomRemoved(index, old.name, old.description);
//        }
    }


    public int numberOfDevices() {
        return devices.size();
    }


    public TabDeviceRoomFragment getDevicesFrag() {
        if (devicesFrag == null) {
            return new TabDeviceRoomFragment(/* blank template */);
        }
        return devicesFrag;
    }

}
