package io.keepcube.kcapp.Fragment.Tab;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;

import io.keepcube.kcapp.Data.Type;
import io.keepcube.kcapp.R;
import io.keepcube.kcapp.Tools.Animation.AnimRecyclerAdapter;
import io.keepcube.kcapp.Tools.Animation.Keyframe;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerPalette;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerSwatch;
import io.keepcube.kcapp.Tools.ItemTouchHelperAdapter;
import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabDashboardFragment extends Fragment {

    public DashRecyclerAdapter adapter = null;
    Fragment fragment = this;
    AppCompatActivity activity = null;
    private String TAG = "TabDashboardFragment";

    public TabDashboardFragment() {

        adapter = new DashRecyclerAdapter();


        // TODO: 20.7.17 načíst z globálních seznamů (stáhnout ze serveru)
        for (int i = 0; i < 19; i++) {
//            adapter.add("Čudl", 0);
            adapter.add(String.format("Ledka %d", i), 0);
//            adapter.add(String.valueOf(i), 0);
        }

        adapter.add("Ledkaaaaaaaaaaaaaaaaaa", 0);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_fragment_dashboard, container, false);
        activity = (AppCompatActivity) getActivity();
        final Context context = getContext();
        final Context c = getContext();

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycler.setItemAnimator(new DefaultItemAnimator());
//        recycler.getRecycledViewPool().setMaxRecycledViews(Type.LED, 0); // TODO: 1.8.17 IMPORTANT!

        recycler.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recycler);

        return view;
    }


    class DashRecyclerAdapter extends RecyclerView.Adapter<DashRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
        static final int ITEM_TYPE_SPACE = -1;

        // TODO: 27.7.17 Zrušit ITEM_TYPE_SPACE, přidat typy pro všechna zařízení

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> numberOfDevices = new ArrayList<>();

//        ArrayList<Object> views = new ArrayList<>();


//        HashMap<Integer, Object> views = new HashMap<>();

        SparseArray<Object> views = new SparseArray<>();


        DashRecyclerAdapter() {
            names.add(0, "null");
            numberOfDevices.add(0, 0);
        }

        public void add(String item, int devices) {

            views.append(0, new Type.Create("Ledka").led());

            Type.Create a = (Type.Create) views.get(0);






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
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(names, i, i + 1);
                    Collections.swap(numberOfDevices, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(names, i, i - 1);
                    Collections.swap(numberOfDevices, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
        }

        @Override
        public int getItemViewType(int position) {
//            return Type.LED;
            return position == 0 ? ITEM_TYPE_SPACE : Type.LED;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public DashRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_room, parent, false);

            if (viewType == Type.LED) {
                View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_dash_led, parent, false);
                final ClassicViewHolder view = new ClassicViewHolder(normalView); // view holder for normal items
                final int position = view.getAdapterPosition();




//                Log.d(TAG, "onCreateViewHolder: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + position);




                view.colorBtnCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final View solidOrAnimLayout = getActivity().getLayoutInflater().inflate(R.layout.di_solid_or_anim, null);

                        CardView solid = (CardView) solidOrAnimLayout.findViewById(R.id.solid_color_btn);
                        CardView anim = (CardView) solidOrAnimLayout.findViewById(R.id.color_anim_btn);

                        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff44336, 0xff9c27b0, 0xff3f51b5, 0xff03a9f4, 0xff009688, 0xff8bc34a, 0xffffeb3b, 0xffff9800});
                        rainbow.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                        anim.setBackground(rainbow);

                        final MaterialDialog solidOrAnimDiag = new MaterialDialog.Builder(getContext())
                                .title("Set LED to...")
                                .customView(solidOrAnimLayout, true /*wrapInScrollView*/)
                                .negativeText(R.string.negative_text)
                                .show();


                        solid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                solidOrAnimDiag.dismiss();


                                final ColorPickerPalette colorPickerPalette = (ColorPickerPalette) View.inflate(getContext(), R.layout.color_picker, null);


                                final MaterialDialog alert = new MaterialDialog.Builder(getContext())
                                        .title(R.string.select_color)
                                        .customView(colorPickerPalette, true /*wrapInScrollView*/)
                                        .negativeText(R.string.negative_text)
                                        .neutralText(R.string.custom)
                                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                                new ChromaDialog.Builder()
                                                        .initialColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                                                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                                                        .onColorSelected(new ColorSelectListener() {
                                                            @Override
                                                            public void onColorSelected(@ColorInt int i) {
//                                                                    items.get(holder.getAdapterPosition()).color = i;
//                                                                    colorViewHolder.color.setBackgroundTintList(ColorStateList.valueOf(i));
//                                                                    colorViewHolder.color.setTextColor(textColorFromBackground(i));
//


                                                                view.colorBtnCard.setBackgroundColor(i);
                                                                view.label.setTextColor(Keyframe.textColorFromBackground(i));
                                                                view.label.setText(R.string.color);
                                                            }
                                                        })
                                                        .create()
                                                        .show(fragment.getFragmentManager(), "ChromaDialog");


                                            }
                                        })
                                        .show();

                                final int[] colors = getContext().getResources().getIntArray(R.array.md_rainbow_500);


                                //(int) Math.ceil(Math.sqrt(colors.length))
                                colorPickerPalette.init(colors.length, 4, new ColorPickerSwatch.OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(int color) {
                                        alert.dismiss();
//                                            colorViewHolder.color.setBackgroundTintList(ColorStateList.valueOf(color));
//                                            colorViewHolder.color.setTextColor(Keyframe.textColorFromBackground(color));
//                                            items.get(holder.getAdapterPosition()).color = color;
//

                                        view.colorBtnCard.setBackgroundColor(color);
                                        view.label.setTextColor(Keyframe.textColorFromBackground(color));
                                        view.label.setText(R.string.color);


                                    }
                                });
                                colorPickerPalette.drawPalette(colors, 0);
                            }
                        });


                        anim.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                solidOrAnimDiag.dismiss();
                                View animLayout = getActivity().getLayoutInflater().inflate(R.layout.di_anim_constructor, null);

                                final AnimRecyclerAdapter animAdapter = new AnimRecyclerAdapter(getContext(), fragment);
                                animAdapter.add(new Keyframe(ContextCompat.getColor(getContext(), R.color.colorPrimary), 1));

                                RecyclerView recycler = (RecyclerView) animLayout.findViewById(R.id.recycler);
                                recycler.setHasFixedSize(true);
                                recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                recycler.setItemAnimator(new DefaultItemAnimator());
                                recycler.setAdapter(animAdapter);

                                new ItemTouchHelper(new ItemTouchHelper.Callback() {
                                    @Override
                                    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                                        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                                        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                                        return makeMovementFlags(dragFlags, swipeFlags);
                                    }

                                    @Override
                                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                        animAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                                        animAdapter.onItemDismiss(viewHolder.getAdapterPosition());
                                    }
                                }).attachToRecyclerView(recycler);

                                animLayout.findViewById(R.id.add_color_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        animAdapter.add(new Keyframe(ContextCompat.getColor(getContext(), R.color.colorPrimary), 1)); // Add color keyframe
                                    }
                                });

                                animLayout.findViewById(R.id.add_wait_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        animAdapter.add(new Keyframe(1)); // Add wait keyframe
                                    }
                                });

                                new MaterialDialog.Builder(getContext())
                                        .title(R.string.animation_constructor)
                                        .customView(animLayout, false /*wrapInScrollView*/)
                                        .positiveText(R.string.positive_text)
                                        .negativeText(R.string.negative_text)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                // TODO: 1.8.17 send animation data to server


                                                view.colorBtnCard.setBackground(animAdapter.getMultiGradient());
                                                view.label.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                                                view.label.setText(R.string.animation);
//                                                    notifyDataSetChanged();

                                            }
                                        })
                                        .show();


                            }
                        });


                    }
                });



















                return view;

            } else if (viewType == ITEM_TYPE_SPACE) {
                View headerRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_nothing, parent, false);
                return new SpaceViewHolder(headerRow); // view holder for header items
            }

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            switch (viewHolder.getItemViewType()) {
                case Type.LED: {


                    final ClassicViewHolder holder = (ClassicViewHolder) viewHolder;
                    holder.roomName.setText(names.get(position));


































                    break;


                }


                case ITEM_TYPE_SPACE:
                    SpaceViewHolder spaceViewHolder = (SpaceViewHolder) viewHolder;
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
            TextView roomName;
            TextView label;
            CardView colorBtnCard;

            ClassicViewHolder(View v) {
                super(v);
                roomName = (TextView) v.findViewById(R.id.name);
                label = (TextView) v.findViewById(R.id.color_txtw_fakebtn_fakelabel);
                colorBtnCard = (CardView) v.findViewById(R.id.card_btn_color_led);
            }
        }
    }


}
