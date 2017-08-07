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


    public TabDeviceRoomFragment getDevicesFrag() {
        if (devicesFrag == null) {
            return new TabDeviceRoomFragment(/* blank template */);
        }
        return devicesFrag;
    }
}
