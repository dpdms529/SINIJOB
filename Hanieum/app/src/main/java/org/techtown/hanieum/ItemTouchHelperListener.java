package org.techtown.hanieum;

public interface ItemTouchHelperListener {
    boolean onMove(int oldPosition, int newPosition);

    void onSwiped(int position, int direction);
}
