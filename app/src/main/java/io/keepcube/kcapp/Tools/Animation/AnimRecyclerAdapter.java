package io.keepcube.kcapp.Tools.Animation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import io.keepcube.kcapp.R;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerPalette;
import io.keepcube.kcapp.Tools.ColorPicker.ColorPickerSwatch;
import io.keepcube.kcapp.Tools.ItemTouchHelperAdapter;
import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

public class AnimRecyclerAdapter extends RecyclerView.Adapter<AnimRecyclerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private Animation animation;
    private Fragment fragment;
    private Context context;

    public AnimRecyclerAdapter(Context context, Fragment fragment) {
        this.animation = new Animation();
        this.fragment = fragment;
        this.context = context;
    }

    public void add(Keyframe keyframe) {
        animation.add(keyframe);
        notifyItemInserted(animation.numberOfKeyframes());
    }

    public void remove(int index) {
        animation.remove(index);
        notifyItemRemoved(index);
    }

    public void setLoop(boolean loop) {
        animation.loop = loop;
    }

    public Animation getResult() {
        return animation;
    }


    public void preset(Animation animation) {
        this.animation = animation;
    }


    @Override
    public void onItemDismiss(int position) {
        this.remove(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        animation.swap(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemViewType(int position) {
        return animation.get(position).type;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AnimRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Keyframe.TYPE_COLOR) {
            final ColorViewHolder holder = new ColorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_anim_color, parent, false));

            holder.color.setOnClickListener(new View.OnClickListener() {
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
                                                    animation.get(holder.getAdapterPosition()).color = i;
                                                    holder.color.setBackgroundTintList(ColorStateList.valueOf(i));
                                                    holder.color.setTextColor(Keyframe.textColorFromBackground(i));
                                                }
                                            }).create().show(fragment.getFragmentManager(), "ChromaDialog");
                                }
                            }).show();

                    final int[] materialRainbow = context.getResources().getIntArray(R.array.md_rainbow_500);
                    colorPickerPalette.init(materialRainbow.length, /* (int) Math.ceil(Math.sqrt(materialRainbow.length)) */ 4, new ColorPickerSwatch.OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int color) {
                            alert.dismiss();
                            holder.color.setBackgroundTintList(ColorStateList.valueOf(color));
                            holder.color.setTextColor(Keyframe.textColorFromBackground(color));
                            animation.get(holder.getAdapterPosition()).color = color;
                        }
                    });
                    colorPickerPalette.drawPalette(materialRainbow, 0);
                }
            });

            holder.wait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View timeLayout = LayoutInflater.from(context).inflate(R.layout.di_secs_picker, null);
                    final SeekBar seek = (SeekBar) timeLayout.findViewById(R.id.seconds_seek_bar);
                    final TextView info = (TextView) timeLayout.findViewById(R.id.secs_info_text_view);

                    info.setText(String.format(context.getString(R.string.time_n_s), animation.get(holder.getAdapterPosition()).time));
                    seek.setProgress(animation.get(holder.getAdapterPosition()).time);
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

                    new MaterialDialog.Builder(context)
                            .title(R.string.select_color)
                            .customView(timeLayout, false /*wrapInScrollView*/)
                            .positiveText(R.string.positive_text)
                            .negativeText(R.string.negative_text)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    holder.wait.setText(String.format(context.getString(R.string.n_seconds), seek.getProgress()));
                                    animation.get(holder.getAdapterPosition()).time = seek.getProgress();
                                }
                            }).show();
                }
            });

            return holder;
        }


        if (viewType == Keyframe.TYPE_WAIT) {
            final WaitViewHolder holder = new WaitViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ri_anim_wait, parent, false));

            holder.wait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View timeLayout = LayoutInflater.from(context).inflate(R.layout.di_secs_picker, null);

                    final SeekBar seek = (SeekBar) timeLayout.findViewById(R.id.seconds_seek_bar);
                    final TextView info = (TextView) timeLayout.findViewById(R.id.secs_info_text_view);

                    info.setText(String.format(context.getString(R.string.time_n_s), animation.get(holder.getAdapterPosition()).time));
                    seek.setProgress(animation.get(holder.getAdapterPosition()).time);
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

                    new MaterialDialog.Builder(context)
                            .title(R.string.select_time)
                            .customView(timeLayout, false /*wrapInScrollView*/)
                            .positiveText(R.string.positive_text)
                            .negativeText(R.string.negative_text)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    holder.wait.setText(String.format(context.getString(R.string.n_seconds), seek.getProgress()));
                                    animation.get(holder.getAdapterPosition()).time = seek.getProgress();
                                }
                            }).show();
                }
            });

            return holder;
        }

        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case Keyframe.TYPE_COLOR:
                ColorViewHolder colorHolder = (ColorViewHolder) viewHolder;
                colorHolder.color.setBackgroundTintList(ColorStateList.valueOf(animation.get(viewHolder.getAdapterPosition()).color));
                colorHolder.color.setTextColor(Keyframe.textColorFromBackground(animation.get(viewHolder.getAdapterPosition()).color));
                colorHolder.wait.setText(String.format(context.getString(R.string.n_seconds), animation.get(viewHolder.getAdapterPosition()).time));
                break;

            case Keyframe.TYPE_WAIT:
                WaitViewHolder waitHolder = (WaitViewHolder) viewHolder;
                waitHolder.wait.setText(String.format(context.getString(R.string.n_seconds), animation.get(viewHolder.getAdapterPosition()).time));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return animation.numberOfKeyframes();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View v) {
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