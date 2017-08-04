package io.keepcube.kcapp.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Room {
    public String name = null;
    public String description = null;


    public Room(@NonNull String name, @Nullable String description) {
        this.name = name;
        this.description = description;
    }


}
