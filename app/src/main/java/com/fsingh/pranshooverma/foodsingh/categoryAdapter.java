package com.fsingh.pranshooverma.foodsingh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by PRANSHOO VERMA on 13/09/2017.
 */

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.ViewHolder> {
   private Context mContext;
    private List<String> categories;
    private List<String> images_data;
    ImageView img_cat;

    public categoryAdapter(Context mContext, List<String> categories,List<String> images) {
        this.mContext=mContext;
        this.categories=categories;
        this.images_data=images;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img;
        TextView txt;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            img=(ImageView) itemView.findViewById(R.id.img);
            txt=(TextView) itemView.findViewById(R.id.text_char_name);
            cardView=(CardView) itemView.findViewById(R.id.card_view);
            Typeface t = Typeface.createFromAsset(txt.getContext().getAssets(), "fonts/android.ttf");
            txt.setTypeface(t);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.cardview_categories,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        img_cat=(ImageView) holder.itemView.findViewById(R.id.img);

        String path=images_data.get(position);

        String name=categories.get(position);

        name = name.toLowerCase();

        String new_name = name.charAt(0)+"";

        new_name = new_name.toUpperCase();

        new_name+=name.substring(1,name.length());

        Glide.with(mContext).load(path).skipMemoryCache(true).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.RESULT).centerCrop().into(holder.img);
        holder.txt.setText(new_name);

        img_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_name=categories.get(position);
                Intent as=new Intent(mContext,menu_category_wise.class);
                Bundle a=new Bundle();
                a.putString("category",category_name);
                as.putExtras(a);
                view.getContext().startActivity(as);

            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
