package org.techtown.hanieum;

import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.Resource;

import java.util.ArrayList;

public class SelfInfoAdapter extends RecyclerView.Adapter<SelfInfoAdapter.ViewHolder> implements OnSelfInfoItemClickListener{
    ArrayList<SelfInfo> items = new ArrayList<>();
    OnSelfInfoItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.self_info_item,viewGroup,false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SelfInfo item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(SelfInfo item){
        items.add(item);
    }

    public void setItems(ArrayList<SelfInfo> items){
        this.items = items;
    }

    public SelfInfo getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, SelfInfo item){
        items.set(position, item);
    }

    public void clearItems(){
        items.clear();
    }

    public void setOnItemClickListener(OnSelfInfoItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void OnItemClick(ViewHolder holder, View view, int position) {
        if(listener != null){
            listener.OnItemClick(holder,view,position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img, moveImg;
        TextView title;
        ConstraintLayout layout;
        public ViewHolder(View itemView, final OnSelfInfoItemClickListener listener){
            super(itemView);
            img = itemView.findViewById(R.id.infoImg);
            moveImg = itemView.findViewById(R.id.moveImg);
            title = itemView.findViewById(R.id.infoTitle);
            layout = itemView.findViewById(R.id.constLayout);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.OnItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }
        public void setItem(SelfInfo item){
            title.setText(item.getTitle());
            if(item.getCode()=="0"){
                img.setImageResource(R.drawable.video);
            }
            if(itemView.getContext().getClass().equals(MainActivity.class)){
                moveImg.setVisibility(View.GONE);
                ViewGroup.LayoutParams viewParmas = img.getLayoutParams();
                viewParmas.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                viewParmas.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                img.setLayoutParams(viewParmas);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
                params.setMargins(0,0,0,0);
                layout.setLayoutParams(params);
            }

        }
    }
}
