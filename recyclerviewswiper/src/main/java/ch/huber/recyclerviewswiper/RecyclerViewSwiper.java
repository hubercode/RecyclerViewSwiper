package ch.huber.recyclerviewswiper;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

/**
 * Handles swipe gestures on a @{@link RecyclerView} instance. Currently LEFT and RIGHT swipes with
 * multiple @{@link SwipeButton} are supported.
 *
 * @author  Michael Huber
 * @version 1.0
 */
public abstract class RecyclerViewSwiper extends ItemTouchHelper.SimpleCallback {

    /**
     * Width of a single @{@link SwipeButton}. Default value is {@value}.
     */
    private int buttonWidth = 200;

    private RecyclerView recyclerView;

    private GestureDetector swipeButtonGestureDetector;

    private List<SwipeButton> swipeButtons;

    private Map<Integer, List<SwipeButton>> swipeButtonsBufferLeft;
    private Map<Integer, List<SwipeButton>> swipeButtonsBufferRight;

    private Queue<Integer> recoverQueue;

    private int swipedItemPosition = -1;
    private float swipeThreshold = 0.5f;

    /**
     * Gesture-Listener will notify when a motion gesture event has occured. For example, when
     * a @{@link SwipeButton} gets pressed.
     */
    private class SwipeButtonGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            for (SwipeButton swipeButton : swipeButtons) {
                if (swipeButton.onClick(e.getX(), e.getY())) {
                    break;
                }
            }

            return true;
        }
    }

    /**
     * Motion-Listener on @{@link RecyclerView} instance.
     */
    private class RecyclerViewTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (swipedItemPosition < 0) {

                // No item is currently swiped

                return false;
            }

            // Detect pressed SwipeButton

            Point point = new Point((int) event.getRawX(), (int) event.getRawY());

            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedItemPosition);
            View swipedItem = swipedViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) {

                if (rect.top < point.y && rect.bottom > point.y) {
                    swipeButtonGestureDetector.onTouchEvent(event);
                } else {

                    // Motion outside of itemview

                    recoverQueue.add(swipedItemPosition);
                    swipedItemPosition = -1;
                    recoverSwipedItem();
                }

            }

            return false;
        }
    }

    /**
     * Creates a new instance of @{@link RecyclerViewSwiper}.
     *
     * @param context      Context environment variable
     * @param recyclerView Reference on which @{@link RecyclerViewSwiper} should be linked to
     */
    public RecyclerViewSwiper(Context context, RecyclerView recyclerView) {
        super(0, LEFT | RIGHT);

        this.recyclerView = recyclerView;

        this.swipeButtons = new ArrayList<>();
        this.swipeButtonsBufferLeft = new HashMap<>();
        this.swipeButtonsBufferRight = new HashMap<>();

        this.swipeButtonGestureDetector = new GestureDetector(context, new SwipeButtonGestureDetectorListener());
        this.recyclerView.setOnTouchListener(new RecyclerViewTouchListener());

        this.recoverQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer integer) {
                return contains(integer) ? false : super.add(integer);
            }
        };

        attachSwipe();
    }

    public void setButtonWidth(int width) {
        this.buttonWidth = width;
    }

    /**
     * Attach Swipe-Implementation to the given @{@link RecyclerView}
     */
    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(this.recyclerView);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Item triggered to be fully "swiped". Handles logic for detecting what item was swiped and
     * saves the current swiped item position. Furthermore it removes all previous swiped items to
     * "unswiped" state (in general, just one single item can be swiped).
     *
     * @param viewHolder Holder instance of the given @{@link RecyclerView}
     * @param direction  Defines in which direction user swiped
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        // Item is fully swiped and triggered to be "swiped"

        int position = viewHolder.getAdapterPosition();

        if (swipedItemPosition != position) {

            // new item was swiped, therefore previous item queue for "unswipe"

            recoverQueue.add(swipedItemPosition);
        }

        swipedItemPosition = position;

        if (direction == LEFT) {

            // Swiped from right to left ( <-- )

            if (swipeButtonsBufferRight.containsKey(swipedItemPosition)) {
                swipeButtons = swipeButtonsBufferRight.get(swipedItemPosition);
            } else {
                swipeButtons.clear();
            }
        }

        if (direction == RIGHT) {

            // Swiped from left to right ( --> )

            if (swipeButtonsBufferLeft.containsKey(swipedItemPosition)) {
                swipeButtons = swipeButtonsBufferLeft.get(swipedItemPosition);
            } else {
                swipeButtons.clear();
            }
        }

        swipeButtonsBufferRight.clear();
        swipeButtonsBufferLeft.clear();
        swipeThreshold = 0.5f * swipeButtons.size() * buttonWidth;
        recoverSwipedItem();

    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        int position = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;

        if (position < 0) {

            // Invalid position

            swipedItemPosition = position;
            return;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            if (dX < 0) {

                // Swipe from right to left ( <-- )

                List<SwipeButton> buffer = new ArrayList<>();

                if (!swipeButtonsBufferRight.containsKey(position)) {

                    // Sets the item-list

                    initSwipeButtonRight(viewHolder, buffer);
                    swipeButtonsBufferRight.put(position, buffer);
                } else {
                    buffer = swipeButtonsBufferRight.get(position);
                }

                // Make sure to swipe just as wide as the width of all pre-defined SwipeButtons together
                // Swipe to the left, means coordinates are negative values!

                translationX = Math.max(dX, buffer.size() * -buttonWidth);

                // Draw defined SwipeButtons on the right sind of the specific item

                drawSwipeButtonsRight(canvas, itemView, buffer, position, translationX);

            } else if (dX > 0) {

                // Swipe from left to right ( --> )

                List<SwipeButton> buffer = new ArrayList<>();

                if (!swipeButtonsBufferLeft.containsKey(position)) {
                    initSwipeButtonLeft(viewHolder, buffer);
                    swipeButtonsBufferLeft.put(position, buffer);
                } else {
                    buffer = swipeButtonsBufferLeft.get(position);
                }

                // Make sure to swipe just as wide as the width of all pre-defined SwipeButtons together
                // Swipe to the right, means coordinates are positive values!

                translationX = Math.min(dX, buffer.size() * buttonWidth);

                // Draw defined SwipeButtons on the left sind of the specific item

                drawSwipeButtonsLeft(canvas, itemView, buffer, position, translationX);
            }
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    /**
     * Fires changed event on currently swiped item, which triggers a re-drawn and resets swiped
     * view.
     */
    private synchronized void recoverSwipedItem() {

        // fire changed event on swiped item, so this is going to re-drawn and resets swipe view to non-swiped

        while (!recoverQueue.isEmpty()) {
            int position = recoverQueue.poll();
            if (position > -1) {
                recyclerView.getAdapter().notifyItemChanged(position);
            }
        }
    }

    /**
     * Draw the given @swipeButtons to the right side of the @itemView.
     *
     * @param canvas       Area where the @swipeButtons gets drawn to
     * @param itemView     Whole view of the item
     * @param swipeButtons List of @{@link SwipeButton} which should be drawn to the given @itemView
     * @param position     A @{@link RecyclerView} position reference
     * @param dX           The amount of horizontal displacement caused by user's action
     */
    private void drawSwipeButtonsRight(Canvas canvas, View itemView, List<SwipeButton> swipeButtons, int position, float dX) {
        float right = itemView.getRight();
        float buttonWidth = (-1) * dX / swipeButtons.size();

        for (SwipeButton swipeButton : swipeButtons) {
            float left = right - buttonWidth;
            swipeButton.onDraw(canvas, new RectF(left, itemView.getTop(), right, itemView.getBottom()), position);
            right = left;
        }
    }

    /**
     * Draw the given @swipeButtons to the left side of the @itemView.
     *
     * @param canvas       Area where the @swipeButtons gets drawn to
     * @param itemView     Whole view of the item
     * @param swipeButtons List of @{@link SwipeButton} which should be drawn to the given @itemView
     * @param position     A @{@link RecyclerView} position reference
     * @param dX           The amount of horizontal displacement caused by user's action
     */
    private void drawSwipeButtonsLeft(Canvas canvas, View itemView, List<SwipeButton> swipeButtons, int position, float dX) {
        float left = itemView.getLeft();
        float buttonWidth = dX / swipeButtons.size();

        for (SwipeButton swipeButton : swipeButtons) {
            float right = left + buttonWidth;
            swipeButton.onDraw(canvas, new RectF(left, itemView.getTop(), right, itemView.getBottom()), position);
            left = right;
        }
    }

    /**
     * Declaration of Items that are drawn on the right side when swiping
     *
     * @param viewHolder   Related Holder of @{@link RecyclerView}
     * @param swipeButtons Items which gets drawn on the right side once the user swipes an item
     */
    public abstract void initSwipeButtonRight(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons);

    /**
     * Declaration of Items that are drawn on the left side when swiping
     *
     * @param viewHolder   Related Holder of @{@link RecyclerView}
     * @param swipeButtons Items which gets drawn on the left side once the user swipes an item
     */
    public abstract void initSwipeButtonLeft(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons);
}
