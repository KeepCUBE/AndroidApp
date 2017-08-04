package io.keepcube.kcapp.Tools.Animation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import io.keepcube.kcapp.R;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerPalette;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerSwatch;
import io.keepcube.kcapp.Tools.ItemTouchHelperAdapter;
import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

public class AnimRecyclerAdapter extends RecyclerView.Adapter<AnimRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    Context context;
    Fragment fragment;
    String TAG = "AnimRecyclerAdapter";
    private ArrayList<Keyframe> items = new ArrayList<>();

    public AnimRecyclerAdapter(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    public void add(Keyframe keyframe) {
        items.add(keyframe);
        notifyItemInserted(items.size());
    }

    public void remove(int index) {
        index++;
        items.remove(index);
        notifyItemRemoved(index);
    }


    @Override
    public void onItemDismiss(int position) {
        this.remove(--position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }


    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

//
//    public int[] getColorList() {
//
//    }
//

    public GradientDrawable getMultiGradient() {
        ArrayList<Integer> colorList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++)
            if (items.get(i).type == Keyframe.TYPE_COLOR)
                colorList.add(items.get(i).color);

        if (colorList.size() < 2 ) {
            colorList.add(colorList.get(0));
        }

        Iterator<Integer> iterator = colorList.iterator();
        int[] colors = new int[colorList.size()];
        for (int i = 0; i < colors.length; i++) colors[i] = iterator.next();

        GradientDrawable multigradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        multigradient.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));
        return multigradient;
    }




    // Create new views (invoked by the layout manager)
    @Override
    public AnimRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Keyframe.TYPE_COLOR)
            return new ColorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_anim_color, parent, false));

        if (viewType == Keyframe.TYPE_WAIT)
            return new WaitViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_anim_wait, parent, false));

        if (viewType == Keyframe.TYPE_NOTHING)
            return new NothingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_nothing, parent, false));

        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case Keyframe.TYPE_COLOR: {
                final ColorViewHolder colorViewHolder = (ColorViewHolder) holder;

                colorViewHolder.color.setBackgroundTintList(ColorStateList.valueOf(items.get(holder.getAdapterPosition()).color));
                colorViewHolder.color.setTextColor(Keyframe.textColorFromBackground(items.get(holder.getAdapterPosition()).color));
                colorViewHolder.wait.setText(String.format(context.getString(R.string.n_seconds), items.get(holder.getAdapterPosition()).time));


                colorViewHolder.color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final ColorPickerPalette colorPickerPalette = (ColorPickerPalette) View.inflate(context, R.layout.color_picker, null);


                        final MaterialDialog alert = new MaterialDialog.Builder(context)
                                .title(R.string.select_color)
                                .customView(colorPickerPalette, true /*wrapInScrollView*/)
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
                                                        items.get(holder.getAdapterPosition()).color = i;
                                                        colorViewHolder.color.setBackgroundTintList(ColorStateList.valueOf(i));
                                                        colorViewHolder.color.setTextColor(Keyframe.textColorFromBackground(i));
                                                    }
                                                })
                                                .create()
                                                .show(fragment.getFragmentManager(), "ChromaDialog");


                                    }
                                })
                                .show();

                        final int[] colors = context.getResources().getIntArray(R.array.md_rainbow_500);


                        //(int) Math.ceil(Math.sqrt(colors.length))
                        colorPickerPalette.init(colors.length, 4, new ColorPickerSwatch.OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int color) {
                                alert.dismiss();
                                colorViewHolder.color.setBackgroundTintList(ColorStateList.valueOf(color));
                                colorViewHolder.color.setTextColor(Keyframe.textColorFromBackground(color));
                                items.get(holder.getAdapterPosition()).color = color;

                            }
                        });
                        colorPickerPalette.drawPalette(colors, 0);


                    }
                });

                colorViewHolder.wait.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("yyyyyyyyyyyyyyy", "yyyyyyyyyyyyyyyyy");


                        View timeLayout = LayoutInflater.from(context).inflate(R.layout.di_secs_picker, null);


                        final SeekBar seek = (SeekBar) timeLayout.findViewById(R.id.seconds_seek_bar);
                        final TextView info = (TextView) timeLayout.findViewById(R.id.secs_info_text_view);

                        seek.setProgress(items.get(holder.getAdapterPosition()).time);
                        info.setText(String.format(context.getString(R.string.time_n_s), items.get(holder.getAdapterPosition()).time));

                        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                info.setText(String.format(context.getString(R.string.time_n_s), progress));
                            }
                        });


                        final MaterialDialog albert = new MaterialDialog.Builder(context)
                                .title(R.string.select_color)
                                .customView(timeLayout, false /*wrapInScrollView*/)
                                .positiveText(R.string.positive_text)
                                .negativeText(R.string.negative_text)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        colorViewHolder.wait.setText(String.format(context.getString(R.string.n_seconds), seek.getProgress()));
                                        items.get(holder.getAdapterPosition()).time = seek.getProgress();
                                    }
                                })
                                .show();


                    }
                });


                break;
            }

            case Keyframe.TYPE_WAIT: {
                final WaitViewHolder waitViewHolder = (WaitViewHolder) holder;

                waitViewHolder.wait.setText(String.format(context.getString(R.string.n_seconds), items.get(holder.getAdapterPosition()).time));

                waitViewHolder.wait.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("zzzzzzzzzzzzzzzz", "zzzzzzzzzzzzzzzzz");


                        View timeLayout = LayoutInflater.from(context).inflate(R.layout.di_secs_picker, null);


                        final SeekBar seek = (SeekBar) timeLayout.findViewById(R.id.seconds_seek_bar);
                        final TextView info = (TextView) timeLayout.findViewById(R.id.secs_info_text_view);

                        seek.setProgress(items.get(holder.getAdapterPosition()).time);
                        info.setText(String.format(context.getString(R.string.time_n_s), items.get(holder.getAdapterPosition()).time));

                        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                info.setText(String.format(context.getString(R.string.time_n_s), progress));
                            }
                        });


                        final MaterialDialog albert = new MaterialDialog.Builder(context)
                                .title(R.string.select_time)
                                .customView(timeLayout, false /*wrapInScrollView*/)
                                .positiveText(R.string.positive_text)
                                .negativeText(R.string.negative_text)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        waitViewHolder.wait.setText(String.format(context.getString(R.string.n_seconds), seek.getProgress()));
                                        items.get(holder.getAdapterPosition()).time = seek.getProgress();
                                    }
                                })
                                .show();


                    }
                });


                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View v) {
            super(v);
        }
    }

    private class NothingViewHolder extends ViewHolder {
        NothingViewHolder(View v) {
            super(v);
        }
    }

    private class ColorViewHolder extends ViewHolder {
        Button color;
        Button wait;

        ColorViewHolder(View v) {
            super(v);
            color = (Button) v.findViewById(R.id.animcolor_btn);
            wait = (Button) v.findViewById(R.id.animtime_btn);
        }
    }

    private class WaitViewHolder extends ViewHolder {
        Button wait;

        WaitViewHolder(View v) {
            super(v);
            wait = (Button) v.findViewById(R.id.waitfor_btn);
        }
    }


}