package com.fsingh.pranshooverma.foodsingh;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PRANSHOO VERMA on 13/09/2017.
 */

public class categoryAdapter_menu_wise extends RecyclerView.Adapter<categoryAdapter_menu_wise.ViewHolder> {

    private Context mContext;
    private List<String> dish_name=new ArrayList<>();
    private List<String> dish_price=new ArrayList<>();
    private List<String> NA = new ArrayList<>();
    boolean check=false;

    ImageView plus,minus;


    public categoryAdapter_menu_wise(Context mContext,List<String> dish_name,List<String> dish_price) {
        this.mContext=mContext;
        this.dish_name=dish_name;
        this.dish_price=dish_price;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView diname,diprice,item_quantity;
        ImageView pl,mi;
        public ViewHolder(View itemView) {
            super(itemView);
            diname=(TextView) itemView.findViewById(R.id.dish_name);
            diprice=(TextView) itemView.findViewById(R.id.dish_price);
            item_quantity=(TextView) itemView.findViewById(R.id.item_quantity);
            pl=(ImageView) itemView.findViewById(R.id.plus);
            mi=(ImageView) itemView.findViewById(R.id.minus);
            Typeface t = Typeface.createFromAsset(diname.getContext().getAssets(), "fonts/android.ttf");
            diname.setTypeface(t);
            diprice.setTypeface(t);
            item_quantity.setTypeface(t);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.cardview_category_menu,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        plus=(ImageView) holder.itemView.findViewById(R.id.plus);
        minus=(ImageView) holder.itemView.findViewById(R.id.minus);

        String name=dish_name.get(position);
        String rupees=dish_price.get(position);

        holder.diname.setText(name);

        holder.diprice.setText(rupees);
        if(rupees.equals("Rs.0")){
            holder.diprice.setText("NA");
           check = true;
        }else{
            check = false;
        }




        if(constants.item_name_deb.contains(name))
        {
            int index=constants.item_name_deb.indexOf(name);
            int qa= Integer.parseInt(constants.item_quant_deb.get(index));
            holder.item_quantity.setText(String.valueOf(qa));
        }


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.diprice.getText().toString().equals("NA")){

                }else{

                //adding to the list
                constants.items_name.add(dish_name.get(position));
                constants.items_price.add(dish_price.get(position));

                //setting the value to the textView for having the quantity
                int a=Integer.parseInt((String) holder.item_quantity.getText())+1;
                holder.item_quantity.setText(String.valueOf(a));

                //showing at the toolbar
                menu_category_wise.cartitemcount.setText(String.valueOf(constants.items_name.size()));
                menu.cartitemcount1.setText(String.valueOf(constants.items_name.size()));
            //    Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();


                //making the quantity count
                if(constants.item_name_deb.contains(dish_name.get(position)))
                {

                    int index= constants.item_name_deb.indexOf(dish_name.get(position));
                    int prev_value= Integer.parseInt(constants.item_quant_deb.get(index));
                    constants.item_quant_deb.set(index,String.valueOf(prev_value+1));
              //      Toast.makeText(mContext, constants.item_name_deb.toString()+"\n"+constants.item_quant_deb, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    //add that item to the arraylist
                    constants.item_name_deb.add(dish_name.get(position));
                    constants.item_quant_deb.add("1");
          //          Toast.makeText(mContext, "added 1", Toast.LENGTH_SHORT).show();
                }

                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.diprice.getText().toString().equals("NA")){

                }else{
                int a=Integer.parseInt((String) holder.item_quantity.getText());
                if(a!=0)
                {
                    a=a-1;
                    holder.item_quantity.setText(String.valueOf(a));
                }


                if(constants.item_name_deb.contains(dish_name.get(position)))
                {

                    int index= constants.item_name_deb.indexOf(dish_name.get(position));
                    int prev_value= Integer.parseInt(constants.item_quant_deb.get(index));

                    if(prev_value==1)
                    {
                        constants.item_name_deb.remove(dish_name.get(position));
                        constants.item_quant_deb.remove(index);
                    }
                    else {
                        constants.item_quant_deb.set(index, String.valueOf(prev_value - 1));
                    }
          //          Toast.makeText(mContext, constants.item_name_deb.toString() + "\n" + constants.item_quant_deb, Toast.LENGTH_SHORT).show();

                }


                if(constants.items_name.contains(dish_name.get(position)))
                {
                    constants.items_name.remove(dish_name.get(position));
                    constants.items_price.remove(dish_price.get(position));
                    menu_category_wise.cartitemcount.setText(String.valueOf(constants.items_name.size()));
                    menu.cartitemcount1.setText(String.valueOf(constants.items_name.size()));
            //        Toast.makeText(mContext, "Removed from cart", Toast.LENGTH_SHORT).show();
                }
                else {
                    //        Toast.makeText(mContext, "You dont have this item in cart", Toast.LENGTH_SHORT).show();
                }        }
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return dish_name.size();
    }
}


///adlasdjlakdjlaskdkj