package io.keepcube.kcapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import io.keepcube.kcapp.Data.Home;
import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomsFragment extends Fragment {
    private String TAG = "RoomsFragment";


    public RoomsFragment() {


        // TODO: 20.7.17 načíst z globálních seznamů (stáhnout ze serveru)

//        adapter.add("Garáž", 8);
//        adapter.add("Koupelna", 7);
//        adapter.add("Půda", 6);
//        adapter.add("Zahrada", 5);
//
//        adapter.add("Ložnice", 4);
//        adapter.add("Kuchyň", 3);
//        adapter.add("Sklep", 2);
//        adapter.add("Obejvák", 1);
//
//        adapter.add("mmmmmmmmmmmmmmm", 9999);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Context context = getContext();


        // Title
        ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar)).setTitle(getString(R.string.rooms));


        // Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Rooms Recycler
        final RoomsRecyclerAdapter adapter = new RoomsRecyclerAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        // manager.setReverseLayout(true);
        // manager.setStackFromEnd(true);
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);


        Home.setOnRoomChangedListener(new Home.OnRoomChangedListener() {
            @Override
            public void onRoomAdded(int position, @NonNull String name, @Nullable String description) {
                adapter.notifyItemInserted(position);
            }

            @Override
            public void onRoomRemoved(int position, @NonNull String name, @Nullable String description) {
                adapter.notifyItemRemoved(position);
            }
        });


        // TODO: FIXME: když se frag objeví podruhé, je nascrollován tam kde byl. (resetovat instanci?)
        // ((AppBarLayout) view.findViewById(R.id.devicesAppBarLay)).setExpanded(false);
        // LinearLayoutManager layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
        // layoutManager.scrollToPositionWithOffset(0, 0);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // když se opětovně zobrazí
    }


    class RoomsRecyclerAdapter extends RecyclerView.Adapter<RoomsRecyclerAdapter.ViewHolder> {
        // Create new views (invoked by the layout manager)
        @Override
        public RoomsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ClassicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_room, parent, false));
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            final ClassicViewHolder holder = (ClassicViewHolder) viewHolder;

            holder.roomName.setText(Home.room(position).name);
            holder.devicesCount.setText("0 smart devices");
            if (Home.room(position).description == null) holder.description.setText(R.string.no_description);
            else holder.description.setText(Home.room(position).description);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.are_you_sure)
                            .content(String.format(getString(R.string.sure_remove_room), holder.roomName.getText()))
                            .positiveText(R.string.remove)
                            .negativeText(R.string.keep)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Home.removeRoom(viewHolder.getAdapterPosition());
                                }
                            })
                            .show();
                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "edid", Toast.LENGTH_SHORT).show();
                    // TODO: 20.7.17 edit room
                }
            });

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "CardView.OnClickListener", Toast.LENGTH_SHORT).show();

                }
            });

        }

        @Override
        public int getItemCount() {
            return Home.numberOfRooms();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View v) {
                super(v);
            }
        }

        class ClassicViewHolder extends ViewHolder {
            CardView card;
            TextView devicesCount;
            TextView roomName;
            TextView description;
            ImageButton delete;
            ImageButton edit;

            ClassicViewHolder(View v) {
                super(v);
                card = (CardView) v.findViewById(R.id.room_recycler_card_item);
                devicesCount = (TextView) v.findViewById(R.id.devides_count);
                roomName = (TextView) v.findViewById(R.id.roomName);
                description = (TextView) v.findViewById(R.id.roomdescription);
                delete = (ImageButton) v.findViewById(R.id.delete);
                edit = (ImageButton) v.findViewById(R.id.edit);
            }
        }
    }
}
