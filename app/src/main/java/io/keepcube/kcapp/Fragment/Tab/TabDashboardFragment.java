package io.keepcube.kcapp.Fragment.Tab;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import io.keepcube.kcapp.Data.Dashboard;
import io.keepcube.kcapp.Data.Device;
import io.keepcube.kcapp.Data.Type;
import io.keepcube.kcapp.R;
import io.keepcube.kcapp.Tools.Animation.AnimRecyclerAdapter;
import io.keepcube.kcapp.Tools.Animation.Animation;
import io.keepcube.kcapp.Tools.Animation.Keyframe;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerPalette;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerSwatch;
import io.keepcube.kcapp.Tools.ItemTouchHelperAdapter;
import io.keepcube.kcapp.Tools.Snacker;
import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabDashboardFragment extends Fragment {
    final DashRecyclerAdapter adapter = new DashRecyclerAdapter();
    private String TAG = "TabDashboardFragment";
    private Context context = getContext();
    private Fragment fragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_fragment_dashboard, container, false);
        context = getContext();

        Dashboard.setOnDeviceChangedListener(adapter);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, 0);
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


    class DashRecyclerAdapter extends RecyclerView.Adapter<DashRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter, Dashboard.OnDeviceChangedListener {
        @Override
        public void onDeviceAdded(int position) {
            notifyItemInserted(++position);
        }

        @Override
        public void onDeviceRemoved(int position) {
            notifyItemRemoved(++position);
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (toPosition == 0) return; // Avoid swapping with first helper item
            Log.d(TAG, "Moving from " + fromPosition + " to " + toPosition);
            Dashboard.swap(fromPosition - 1, toPosition - 1);
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(final int position) {
            if (getView() != null) {
                Dashboard.unregisterDevice(position - 1);
                Snacker.make(getView().getRootView().findViewById(R.id.coordinatorfabholder),
                        getString(R.string.device_removed_from_dash), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dashboard.restoreLastUnregistered();
                            }
                        })
                        .show();
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? Type.HELPER : Dashboard.getDevice(--position).getType();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public DashRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            int layout = R.layout.ri_nothing;

            switch (viewType) {
                case Type.LED:
                    layout = R.layout.ri_dash_led;
                    break;

                case Type.DIMMER:
                    layout = R.layout.ri_dash_dimmer;
                    break;

                case Type.HELPER:
                    layout = R.layout.ri_nothing;
                    break;
            }

            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

            switch (viewType) {
                case Type.LED:
                    return new LedViewHolder(view);

                case Type.DIMMER:
                    return new DimmerViewHolder(view);

                case Type.HELPER:
                    return new SpaceViewHolder(view);
            }

            return null;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case Type.LED: {
                    ((LedViewHolder) holder).bind(position);
                    break;
                }

                case Type.DIMMER: {
                    ((DimmerViewHolder) holder).bind(position);
                    break;
                }

                case Type.HELPER:
                    SpaceViewHolder spaceViewHolder = (SpaceViewHolder) holder;
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return Dashboard.numberOfDevices() + 1;
        }

        private void universalActionsDialog(final int position) {
            View view = View.inflate(context, R.layout.di_actions, null);

            final EditText name = (EditText) view.findViewById(R.id.input_name_action);
            name.setText(Dashboard.getDevice(position - 1).getName());

            final MaterialDialog actionsDialog = new MaterialDialog.Builder(context)
                    .title("Actions")
                    .customView(view, false)
                    .negativeText(R.string.negative_text)
                    .positiveText(R.string.positive_text)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Dashboard.getDevice(position - 1).setName(name.getText().toString());
                            notifyDataSetChanged();
                        }
                    }).show();

            view.findViewById(R.id.remove_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionsDialog.dismiss();
                    adapter.onItemDismiss(position);
                }
            });

            view.findViewById(R.id.group_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionsDialog.dismiss();
                    Toast.makeText(context, "Not implemented yet :(", Toast.LENGTH_SHORT).show();
                }
            });
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

        class LedViewHolder extends ViewHolder {
            CardView base;
            TextView name;
            TextView subname;
            TextView label;
            CardView colorBtnCard;

            LedViewHolder(View v) {
                super(v);
                final LedViewHolder view = this;
                base = (CardView) v.findViewById(R.id.base_card);
                name = (TextView) v.findViewById(R.id.name);
                subname = (TextView) v.findViewById(R.id.subname);
                label = (TextView) v.findViewById(R.id.color_txtw_fakebtn_fakelabel);
                colorBtnCard = (CardView) v.findViewById(R.id.card_btn_color_led);

                base.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        universalActionsDialog(view.getAdapterPosition());

                    }
                });

                colorBtnCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View solidOrAnimLayout = getActivity().getLayoutInflater().inflate(R.layout.di_solid_or_anim, null);

                        CardView solid = (CardView) solidOrAnimLayout.findViewById(R.id.solid_color_btn);
                        final CardView anim = (CardView) solidOrAnimLayout.findViewById(R.id.color_anim_btn);

                        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff44336, 0xff9c27b0, 0xff3f51b5, 0xff03a9f4, 0xff009688, 0xff8bc34a, 0xffffeb3b, 0xffff9800});
                        rainbow.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                        anim.setBackground(rainbow);

                        final MaterialDialog solidOrAnimDiag = new MaterialDialog.Builder(getContext())
                                .title(R.string.set_led_to)
                                .customView(solidOrAnimLayout, true /*wrapInScrollView*/)
                                .negativeText(R.string.negative_text)
                                .show();

                        solid.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                solidOrAnimDiag.dismiss();

                                final ColorPickerPalette colorPickerPalette = (ColorPickerPalette) View.inflate(context, R.layout.color_picker, null);
                                final MaterialDialog colorSelectDialog = new MaterialDialog.Builder(getContext())
                                        .customView(colorPickerPalette, true /*wrapInScrollView*/)
                                        .title(R.string.select_color)
                                        .negativeText(R.string.negative_text)
                                        .neutralText(R.string.custom)
                                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                new ChromaDialog.Builder()
                                                        .initialColor(ContextCompat.getColor(context, R.color.colorPrimary))
                                                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                                                        .onColorSelected(new ColorSelectListener() {
                                                            @Override
                                                            public void onColorSelected(@ColorInt int i) {
                                                                ((Device.Led) Dashboard.getDevice(view.getAdapterPosition() - 1)).setSolidColor(i);
                                                                notifyDataSetChanged();
                                                            }
                                                        }).create().show(fragment.getFragmentManager(), "ChromaDialog");
                                            }
                                        }).show();

                                final int[] materialRainbow = context.getResources().getIntArray(R.array.md_rainbow_500);
                                colorPickerPalette.init(materialRainbow.length, /* (int) Math.ceil(Math.sqrt(materialRainbow.length)) */ 4, new ColorPickerSwatch.OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(int color) {
                                        ((Device.Led) Dashboard.getDevice(view.getAdapterPosition() - 1)).setSolidColor(color);
                                        notifyDataSetChanged();
                                        colorSelectDialog.dismiss();
                                    }
                                });
                                colorPickerPalette.drawPalette(materialRainbow, 0);
                            }
                        });

                        anim.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                solidOrAnimDiag.dismiss();
                                View animLayout = getActivity().getLayoutInflater().inflate(R.layout.di_anim_constructor, null);

                                final AnimRecyclerAdapter animAdapter = new AnimRecyclerAdapter(context, fragment);

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

                                CheckBox doLoop = (CheckBox) animLayout.findViewById(R.id.loop_chckbx);

                                Animation lastAnimation = ((Device.Led) Dashboard.getDevice(view.getAdapterPosition() - 1)).getAnimation();

                                if (lastAnimation != null) {
                                    animAdapter.preset(lastAnimation);
                                    doLoop.setChecked(lastAnimation.loop);
                                } else {
                                    animAdapter.add(new Keyframe(ContextCompat.getColor(context, R.color.colorPrimary), 1));
                                }

                                doLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        animAdapter.setLoop(isChecked);
                                    }
                                });

                                animLayout.findViewById(R.id.add_color_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        animAdapter.add(new Keyframe(ContextCompat.getColor(context, R.color.colorPrimary), 1)); // Add color keyframe
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
                                        .autoDismiss(false)
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                // TODO: 1.8.17 send animation data to server

                                                if (animAdapter.getItemCount() < 2) {
                                                    Toast.makeText(context, R.string.no_2_keyframes, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Animation animation = animAdapter.getResult(); // TODO: 9.8.17 ulozit k device
                                                    Device.Led d = ((Device.Led) Dashboard.getDevice(view.getAdapterPosition() - 1));
                                                    d.setAnimation(animation);
                                                    notifyDataSetChanged();
                                                    // TODO: 12.8.17 view.label.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                                                    dialog.dismiss();
                                                }
                                            }
                                        }).show();
                            }
                        });
                    }
                });
            }

            public void bind(int position) {
                Device.Led device = (Device.Led) Dashboard.getDevice(position - 1);
                name.setText(device.getName());
                subname.setText(Dashboard.getParentRoom(position - 1).name);
                label.setText(device.getLabel(getContext()));

                switch (device.getCharacter()) {
                    case Device.Led.CHARACTER_ANIM:
                        colorBtnCard.setBackground(device.getAnimation().generateMultigradient(getContext()));
                        label.setTextColor(0xffffffff); // TODO: 11.8.17 nejak vymyslet z multigradientu
                        break;

                    case Device.Led.CHARACTER_SOLID:
                        int color = device.getSolidColor();
                        GradientDrawable dw = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{color, color}); // TODO: 10.8.17 ?!
                        dw.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics()));
                        colorBtnCard.setBackground(dw);
                        label.setTextColor(Keyframe.textColorFromBackground(color));
                        break;
                }
            }
        }

        class DimmerViewHolder extends ViewHolder {
            CardView base;
            TextView name;
            TextView subname;
            SeekBar seek;
            TextView output;

            DimmerViewHolder(View v) {
                super(v);
                final DimmerViewHolder view = this;
                base = (CardView) v.findViewById(R.id.base_card);
                name = (TextView) v.findViewById(R.id.name);
                subname = (TextView) v.findViewById(R.id.subname);
                seek = (SeekBar) v.findViewById(R.id.IntensitySeek);
                output = (TextView) v.findViewById(R.id.intensityOutputTextView);

                base.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        universalActionsDialog(view.getAdapterPosition());
                    }
                });

                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            Device.Dimmer device = (Device.Dimmer) Dashboard.getDevice(view.getAdapterPosition() - 1);
                            device.setIntenity(progress);
                            output.setText(device.getLabel(context)); // Instead of notifyDataSetChanged() - performance issue
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
            }

            public void bind(int position) {
                Device.Dimmer device = (Device.Dimmer) Dashboard.getDevice(position - 1);
                name.setText(device.getName());
                subname.setText(Dashboard.getParentRoom(position - 1).name);
                seek.setProgress(device.getIntenity());
                output.setText(device.getLabel(getContext()));
            }
        }


    }
}
