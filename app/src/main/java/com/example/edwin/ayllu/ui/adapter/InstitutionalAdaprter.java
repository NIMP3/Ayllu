package com.example.edwin.ayllu.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.Monitoreo;

import java.util.ArrayList;

/**
 * Created by steven on 27/11/16.
 */

public class InstitutionalAdaprter extends RecyclerView.Adapter<InstitutionalAdaprter.MonitoreoHolder> implements View.OnClickListener {
    private ArrayList<Monitoreo> monitoreos;
    private View.OnClickListener listener;
    Context context;

    public InstitutionalAdaprter(Context context) {
        this.context = context;
        this.monitoreos = new ArrayList<>();
    }

    public InstitutionalAdaprter(ArrayList<Monitoreo> monitoreos) {
        this.monitoreos = monitoreos;
    }

    @Override
    public MonitoreoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.monitoreos,parent,false);
        v.setOnClickListener(this);
        MonitoreoHolder holder = new MonitoreoHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MonitoreoHolder holder, int position) {
        holder.date.setText(""+monitoreos.get(position).getDate());
        holder.variable.setText(""+monitoreos.get(position).getVariable());
    }

    @Override
    public int getItemCount() {
        return monitoreos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null) listener.onClick(view);
    }


    public static class MonitoreoHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView variable;
        private String mItem;

        public MonitoreoHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            variable = (TextView) itemView.findViewById(R.id.variable);
        }

    }

    //----------------------------------------------------------------------------------------------
    public void clear(){
        this.monitoreos.clear();
        notifyDataSetChanged();
    }

    public void addAll(@NonNull ArrayList<Monitoreo> monitoreos){
        if(monitoreos == null)
            throw new NullPointerException("Los items no pueden ser nulos");
        this.monitoreos.addAll(monitoreos);
        notifyItemRangeInserted(getItemCount()-1, monitoreos.size());
    }
}