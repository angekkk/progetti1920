package it.uniba.di.piu1920.healthapp.recycler;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.piu1920.healthapp.R;
import it.uniba.di.piu1920.healthapp.classes.Item;

//check del 22/06
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    private List<Item> listahome = new ArrayList<>();

    public RVAdapter(List<Item> listahomet) {
        this.listahome = listahomet;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("Bind [" + holder + "] - Pos [" + position + "]");
        Item c = listahome.get(position);
        holder.name.setText(c.name);
        holder.image.setImageDrawable(c.getImage());
    }

    @Override
    public int getItemCount() {
        return listahome.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}



