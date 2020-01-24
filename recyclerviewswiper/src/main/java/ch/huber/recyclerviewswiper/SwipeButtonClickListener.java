package ch.huber.recyclerviewswiper;

/**
 * Interface for onClick Handler on @{@link SwipeButton} instance
 */
public interface SwipeButtonClickListener {

    /**
     * Fires an onClick event to registered subscribers
     *
     * @param position Current item position in @{@link androidx.recyclerview.widget.RecyclerView}
     */
    void onClick(int position);

}
