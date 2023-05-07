package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private ArrayList<String> mDataset;
    private Activity activity;
    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        // Constructor
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    // 커스텀 어답터 생성자.
    public GalleryAdapter(Activity activity, ArrayList<String> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    // 3가지 Override Method를 작성해야 한다.
    // onCreateViewHolder. ViewGroup parent는 xml에서 만들어 놓은 recyclerView다.
    //@NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // recyclerView(parent) 내에 새로운 view를 만들자.
        // blue print는 아까 만들어 놓은 list_item.xml이다. (R.layout.list.item)
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);

        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("profilePath", mDataset.get(galleryViewHolder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        });
        return galleryViewHolder;
    }

    // onBindViewHolder를 통해 데이터를 set한다.
    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = cardView.findViewById(R.id.imageView);
        Glide.with(activity).load(mDataset.get(position)).centerCrop().override(500).into(imageView);
    }

    // adapter에 의해 가장 먼저 실행될 메소드.
    // 아이템의 개수를 반환한다.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}