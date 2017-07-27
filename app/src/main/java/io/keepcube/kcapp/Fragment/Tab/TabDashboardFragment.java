package io.keepcube.kcapp.Fragment.Tab;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabDashboardFragment extends Fragment {

    public DashRecyclerAdapter adapter = null;

    public TabDashboardFragment() {

        adapter = new DashRecyclerAdapter();

        // TODO: 20.7.17 načíst z globálních seznamů (stáhnout ze serveru)
        for (int i = 0; i < 20; i++) {
            adapter.add("x", 0);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_fragment_dashboard, container, false);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        final Context context = getContext();

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new SpaceAroundAll(4)); // 4 (java) + 4 (xml) = 8 (margin)
        recycler.setAdapter(adapter);

        return view;
    }


    private class SpaceAroundAll extends RecyclerView.ItemDecoration {
        private int space;

        SpaceAroundAll(int space) {
            this.space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space, getResources().getDisplayMetrics()); // Convert dp to px
        }

        @Override
        public void getItemOffsets(Rect margin, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildLayoutPosition(view);
            int max = state.getItemCount();

            if (pos == 0 || pos == 1) margin.top = space; // First two
            if (pos == max || pos == max - 1) margin.bottom = space; // Last two
            if (pos % 2 == 0) margin.left = space; // Every left
            if (pos % 2 == 1) margin.right = space; // Every right
        }
    }


    class DashRecyclerAdapter extends RecyclerView.Adapter<DashRecyclerAdapter.ViewHolder> {
        static final int ITEM_TYPE_CLASSIC = 0;
        static final int ITEM_TYPE_SPACE = 1;

        // TODO: 27.7.17 Zrušit ITEM_TYPE_SPACE, přidat typy pro všechna zařízení

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> numberOfDevices = new ArrayList<>();

        DashRecyclerAdapter() {
        }

        public void add(String item, int devices) {
            names.add(0, item);
            numberOfDevices.add(0, devices);
            notifyItemInserted(0);
        }

        public void remove(String name) {
            int position = names.indexOf(name);
            names.remove(position);
            numberOfDevices.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemViewType(int position) {
            return ITEM_TYPE_CLASSIC;
//            return position == 0 ? ITEM_TYPE_SPACE : ITEM_TYPE_CLASSIC;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public DashRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_room, parent, false);

            if (viewType == ITEM_TYPE_CLASSIC) {
                View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_dash_led, parent, false);
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
//                    classicViewHolder.roomName.setText(names.get(position));
//                    classicViewHolder.numberOfDevices.setText(String.valueOf(numberOfDevices.get(position)));
//
//                    classicViewHolder.delete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            new MaterialDialog.Builder(getContext())
//                                    .title(R.string.are_you_sure)
//                                    .content(String.format(getString(R.string.sure_remove_room), classicViewHolder.roomName.getText()))
//                                    .positiveText(R.string.remove)
//                                    .negativeText(R.string.keep)
//                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                        @Override
//                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                            adapter.remove(String.valueOf(classicViewHolder.roomName.getText()));
////                                            Toast.makeText(getContext(), "rimuvink", Toast.LENGTH_SHORT).show();
//                                            // TODO: 20.7.17 dat vedet serveru o zmene
//                                        }
//                                    })
//                                    .show();
//                        }
//                    });
//
//
//                    classicViewHolder.edit.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(getContext(), "edid", Toast.LENGTH_SHORT).show();
//                            // TODO: 20.7.17 dat vedet serveru o zmene
//                        }
//                    });
//
//
//                    classicViewHolder.card.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(getContext(), "CardView.OnClickListener", Toast.LENGTH_SHORT).show();
//                            // TODO: 20.7.17 materialdialog s nejakýma informacema a tak
//                        }
//                    });


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
//            CardView card;
//            TextView numberOfDevices;
//            TextView roomName;
//            ImageButton delete;
//            ImageButton edit;

            ClassicViewHolder(View v) {
                super(v);
//                card = (CardView) v.findViewById(R.id.room_recycler_card_item);
//                numberOfDevices = (TextView) v.findViewById(R.id.numberOfDevices);
//                roomName = (TextView) v.findViewById(R.id.roomName);
//                delete = (ImageButton) v.findViewById(R.id.delete);
//                edit = (ImageButton) v.findViewById(R.id.edit);
            }
        }
    }


}
