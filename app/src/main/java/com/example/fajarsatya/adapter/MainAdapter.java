package com.example.fajarsatya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fajarsatya.R;
import com.example.fajarsatya.object.Response;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainAdapterViewHolder>{
    private List<Response.Item> data;
    private Context context;

    public MainAdapter(List<Response.Item> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MainAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_layout_item, parent ,false);
        return new MainAdapter.MainAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MainAdapterViewHolder holder, int position) {
        Response.Item res =data.get(position);
        holder.setDetail(res);

        // do anything if there is something else
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MainAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView item;
        ImageView img;

        public MainAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            img = itemView.findViewById(R.id.img_item);
        }

        public void setDetail(Response.Item res){
            item.setText(res.getLogin());
            Picasso.get().load(res.getAvatarUrl()).into(img);
        }
    }

    public void addMoreData(List<Response.Item> datas){
        for (Response.Item res : datas){
            data.add(res);
        }
        notifyDataSetChanged();
    }
}
