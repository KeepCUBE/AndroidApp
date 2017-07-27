package io.keepcube.kcapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomsFragment extends Fragment {

    public RoomsRecyclerAdapter adapter = null;


    public RoomsFragment() {
        adapter = new RoomsRecyclerAdapter();

        // TODO: 20.7.17 načíst z globálních seznamů (stáhnout ze serveru)

        adapter.add("Garáž", 8);
        adapter.add("Koupelna", 7);
        adapter.add("Půda", 6);
        adapter.add("Zahrada", 5);

        adapter.add("Ložnice", 4);
        adapter.add("Kuchyň", 3);
        adapter.add("Sklep", 2);
        adapter.add("Obejvák", 1);

        adapter.add("mmmmmmmmmmmmmmm", 9999);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Context context = getContext();


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar)).setTitle(getString(R.string.rooms));

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        // TODO: FIXME: když se frag objeví podruhé, je nascrollován tam kde byl.
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


    public ArrayList<String> getRoomsNamesList() {
        ArrayList<String> ret = new ArrayList<>(adapter.names); // duplicate list of rooms names
        ret.remove(0); // smazat ten 'null' ošul
        return ret;
    }


    public class RoomsRecyclerAdapter extends RecyclerView.Adapter<RoomsRecyclerAdapter.ViewHolder> {
        static final int ITEM_TYPE_CLASSIC = 0;
        static final int ITEM_TYPE_SPACE = 1;

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> numberOfDevices = new ArrayList<>();

        RoomsRecyclerAdapter() {
            // Adding some helping items
            names.add(0, "null");
            numberOfDevices.add(0, 0);
        }

        public void add(String item, int devices) {
            names.add(1, item);
            numberOfDevices.add(1, devices);
            notifyItemInserted(1);
        }

        public void remove(String name) {
            int position = names.indexOf(name);
            names.remove(position);
            numberOfDevices.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? ITEM_TYPE_SPACE : ITEM_TYPE_CLASSIC;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RoomsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_room, parent, false);

            if (viewType == ITEM_TYPE_CLASSIC) {
                View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_room, parent, false);
                return new ClassicViewHolder(normalView); // view holder for normal items
            } else if (viewType == ITEM_TYPE_SPACE) {
                View headerRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_space_7dp, parent, false);
                return new SpaceViewHolder(headerRow); // view holder for header items
            }

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            switch (holder.getItemViewType()) {
                case ITEM_TYPE_CLASSIC:
                    final ClassicViewHolder classicViewHolder = (ClassicViewHolder) holder;
                    classicViewHolder.roomName.setText(names.get(position));
                    classicViewHolder.numberOfDevices.setText(String.valueOf(numberOfDevices.get(position)));

                    classicViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MaterialDialog.Builder(getContext())
                                    .title(R.string.are_you_sure)
                                    .content(String.format(getString(R.string.sure_remove_room), classicViewHolder.roomName.getText()))
                                    .positiveText(R.string.remove)
                                    .negativeText(R.string.keep)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            adapter.remove(String.valueOf(classicViewHolder.roomName.getText()));
//                                            Toast.makeText(getContext(), "rimuvink", Toast.LENGTH_SHORT).show();
                                            // TODO: 20.7.17 dat vedet serveru o zmene
                                        }
                                    })
                                    .show();
                        }
                    });


                    classicViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "edid", Toast.LENGTH_SHORT).show();
                            // TODO: 20.7.17 dat vedet serveru o zmene
                        }
                    });


                    classicViewHolder.card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "CardView.OnClickListener", Toast.LENGTH_SHORT).show();
                            // TODO: 20.7.17 materialdialog s nejakýma informacema a tak
                        }
                    });


                    break;

                case ITEM_TYPE_SPACE:
                    SpaceViewHolder spaceViewHolder = (SpaceViewHolder) holder;
                    break;
            }
        }


        @Override
        public int getItemCount() {
            return names.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View v) {
                super(v);
            }
        }

        class SpaceViewHolder extends ViewHolder {
            SpaceViewHolder(View v) {
                super(v);
            }
        }

        class ClassicViewHolder extends ViewHolder {
            CardView card;
            TextView numberOfDevices;
            TextView roomName;
            ImageButton delete;
            ImageButton edit;

            ClassicViewHolder(View v) {
                super(v);
                card = (CardView) v.findViewById(R.id.room_recycler_card_item);
                numberOfDevices = (TextView) v.findViewById(R.id.numberOfDevices);
                roomName = (TextView) v.findViewById(R.id.roomName);
                delete = (ImageButton) v.findViewById(R.id.delete);
                edit = (ImageButton) v.findViewById(R.id.edit);
            }
        }
    }
}
