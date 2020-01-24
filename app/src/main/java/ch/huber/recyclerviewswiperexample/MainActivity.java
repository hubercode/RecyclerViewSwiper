package ch.huber.recyclerviewswiperexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.huber.recyclerviewswiper.RecyclerViewSwiper;
import ch.huber.recyclerviewswiper.SwipeButton;
import ch.huber.recyclerviewswiper.SwipeButtonClickListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        initData();
        initRecyclerView();
        addSwiper();
    }

    private void setViews() {
        this.recyclerView = findViewById(R.id.recyclerView);
    }

    private void initData() {
        this.items = getDummyData();
    }

    private void initRecyclerView() {

        this.recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.setLayoutManager(layoutManager);

        this.adapter = new MyAdapter(this.items);
        this.recyclerView.setAdapter(this.adapter);

    }

    private void addSwiper() {

        RecyclerViewSwiper swiper = new RecyclerViewSwiper(this, this.recyclerView) {
            @Override
            public void initSwipeButtonRight(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons) {

                // DELETE

                swipeButtons.add(new SwipeButton(MainActivity.this, "DELETE", Color.RED, new SwipeButtonClickListener() {
                    @Override
                    public void onClick(int position) {
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }));

                // COPY

                swipeButtons.add(new SwipeButton(MainActivity.this, "COPY", Color.BLACK, new SwipeButtonClickListener() {
                    @Override
                    public void onClick(int position) {
                        items.add(items.get(position));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, items.get(position) + " added", Toast.LENGTH_SHORT).show();
                    }
                }));
            }

            @Override
            public void initSwipeButtonLeft(RecyclerView.ViewHolder viewHolder, List<SwipeButton> swipeButtons) {

                // INFO

                swipeButtons.add(new SwipeButton(MainActivity.this, "INFO", Color.BLUE, new SwipeButtonClickListener() {
                    @Override
                    public void onClick(int position) {
                        Toast.makeText(MainActivity.this, items.get(position), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                }));
            }
        };
        swiper.setButtonWidth(200);

    }

    private List<String> getDummyData() {
        List<String> data = new ArrayList<>();
        data.add("Jason Garrett");
        data.add("Nicole Tran");
        data.add("William Larson");
        data.add("Emma Thompson");
        data.add("John Reid");
        data.add("Andrea Walker");
        return data;
    }
}
