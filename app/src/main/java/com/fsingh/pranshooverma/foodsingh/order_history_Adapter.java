package com.fsingh.pranshooverma.foodsingh;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PRANSHOO VERMA on 15/09/2017.
 */

public class order_history_Adapter extends RecyclerView.Adapter<order_history_Adapter.ViewHolder> {

    Context mContext;
    List<String> date=new ArrayList<>();
    List<String> amount=new ArrayList<>();
    List<String> address=new ArrayList<>();
    int counter=0;
    List<String> orders=new ArrayList<>();

    public order_history_Adapter(Context mContext, List<String> date, List<String> amount, List<String> address, List<String> orders) {
        this.mContext = mContext;
        this.date = date;
        this.amount = amount;
        this.address = address;
        this.orders = orders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView date_text,amount_text,orders_text,address_text,order_qt;

        public ViewHolder(View itemView)
        {
            super(itemView);

            date_text=(TextView) itemView.findViewById(R.id.date);
            order_qt=(TextView) itemView.findViewById(R.id.item_quantity);
            amount_text=(TextView) itemView.findViewById(R.id.textView_amount);
            orders_text=(TextView) itemView.findViewById(R.id.textview_scroll_items);
            address_text=(TextView) itemView.findViewById(R.id.textview_scroll_address);
            Typeface t = Typeface.createFromAsset(date_text.getContext().getAssets(), "fonts/android.ttf");
            date_text.setTypeface(t);
            amount_text.setTypeface(t);
            orders_text.setTypeface(t);
            order_qt.setTypeface(t);
            address_text.setTypeface(t);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(mContext).inflate(R.layout.cardview_order_history,parent,false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String d=date.get(holder.getAdapterPosition());
        String a=amount.get(holder.getAdapterPosition());
        String o=orders.get(holder.getAdapterPosition());
        String add=address.get(holder.getAdapterPosition());

        holder.date_text.setText(d);
        holder.amount_text.setText(a);
        holder.orders_text.setText(o);
        holder.address_text.setText(add);
        holder.order_qt.setText((holder.getAdapterPosition()+1)+"");


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return amount.size();
    }
}
