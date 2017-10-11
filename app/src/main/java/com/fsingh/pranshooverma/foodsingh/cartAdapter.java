package com.fsingh.pranshooverma.foodsingh;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class cartAdapter extends RecyclerView.Adapter<cartAdapter.ViewHolder> {
    private Context mContext;
    private List<String> item_name=new ArrayList<>();
    private List<String> item_price=new ArrayList<>();
    ImageView delete;


    public cartAdapter(Context mContext,List<String> dish_name,List<String> dish_price) {
        this.mContext=mContext;
        this.item_name=dish_name;
        this.item_price=dish_price;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView diname,diprice;
        ImageView del;
        public ViewHolder(View itemView) {
            super(itemView);
            diname=(TextView) itemView.findViewById(R.id.item_name);
            diprice=(TextView) itemView.findViewById(R.id.item_price);
            del=(ImageView) itemView.findViewById(R.id.delete);
            Typeface t = Typeface.createFromAsset(diname.getContext().getAssets(), "fonts/android.ttf");
            diname.setTypeface(t);
            diprice.setTypeface(t);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.cardview_cart,parent,false);
        ViewHolder a=new ViewHolder(v);
        return a;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        delete= (ImageView) holder.itemView.findViewById(R.id.delete);

        String n=item_name.get(position);
        String p=item_price.get(position);

        holder.diname.setText(n);
        holder.diprice.setText(p);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int index=constants.item_name_deb.indexOf(item_name.get(position));

                int prev_value= Integer.parseInt(constants.item_quant_deb.get(index));

                if(prev_value==1)
                {
                    constants.item_name_deb.remove(item_name.get(position));
                    constants.item_quant_deb.remove(index);
                }
                else
                {
                    constants.item_quant_deb.set(index, String.valueOf(prev_value-1));
                }
        //        Toast.makeText(mContext, constants.item_name_deb.toString() + "\n" + constants.item_quant_deb, Toast.LENGTH_SHORT).show();

                constants.items_name.remove(item_name.get(position));
                constants.items_price.remove(item_price.get(position));

                menu_category_wise.cartitemcount.setText(String.valueOf(constants.items_name.size()));
                menu.cartitemcount1.setText(String.valueOf(constants.items_name.size()));



                notifyDataSetChanged();



            }
        });

    }

    @Override
    public int getItemCount() {
        return item_name.size();
    }




}
