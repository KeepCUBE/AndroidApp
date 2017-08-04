package io.keepcube.kcapp.Tools;

/**
 * Created by ondrej on 28.7.17.
 */

public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}