package ch.huber.recyclerviewswiper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Represents a single Swipe-Item. This can be added to any direction (LEFT or RIGHT) of
 * a @{@link androidx.recyclerview.widget.RecyclerView}.
 */
public class SwipeButton {

    private Context context;

    private String text;
    private int color;
    private int position;

    private RectF clickRegion;
    private SwipeButtonClickListener clickListener;

    /**
     * Instantiates a single instance of @{@link SwipeButton}
     *
     * @param context       Defines environment context
     * @param text          String text of @{@link SwipeButton}
     * @param color         Ressource identifier of background color
     * @param clickListener Detects a @{@link SwipeButton} click
     */
    public SwipeButton(Context context, String text, int color, SwipeButtonClickListener clickListener) {
        this.context = context;
        this.text = text;
        this.color = color;
        this.clickListener = clickListener;
    }

    /**
     * Checks if the given coordinates are within this @{@link SwipeButton} instance
     *
     * @param x Coordinate on horizontal axis
     * @param y Coordinate on vertical axis
     * @return Wether the click was in the current @{@link SwipeButton} instance or not
     */
    public boolean onClick(float x, float y) {

        if (clickRegion != null && clickRegion.contains(x, y)) {

            clickListener.onClick(position);

            return true;
        }
        return false;
    }

    /**
     *
     * @param canvas   Surface on which objects are drawn
     * @param rect     Item area of @{@link SwipeButton}
     * @param position Current item position within the @{@link androidx.recyclerview.widget.RecyclerView}
     */
    public void onDraw(Canvas canvas, RectF rect, int position) {

        Paint paint = new Paint();

        // Background
        paint.setColor(color);
        canvas.drawRect(rect, paint);

        // Draw Text
        paint.setColor(Color.WHITE);
        paint.setTextSize((12 * Resources.getSystem().getDisplayMetrics().density));

        Rect r = new Rect();
        float cHeight = rect.height();
        float cWidth = rect.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, rect.left + x, rect.top + y, paint);

        // Draw Icon
        /*
        if (icon != null) {

            float x = rect.width() / 2f - (icon.getWidth() / 2f);
            float y = rect.height() / 2f - (icon.getHeight() / 2f);

            // Align icons centered in @SwipeButton

            canvas.drawBitmap(icon, rect.left + x, rect.top + y, null);
        }
         */

        clickRegion = rect;
        this.position = position;

    }

}
