package org.techtown.hanieum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class JobDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnJobDialogClickListener {

    ArrayList<JobDialogItem> items = new ArrayList<>();
    OnJobDialogClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.job_dialog_item, viewGroup, false);
//        context = inflater.getContext();
//        db = AppDatabase.getInstance(context);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        JobDialogItem item = items.get(position);
        ((JobDialogAdapter.ViewHolder) viewHolder).setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void OnItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.OnItemClick(holder, view, position);
        }
    }

    public void setItemClickListener(OnJobDialogClickListener listener) {
        this.listener = listener;
    }

    public void addItem(JobDialogItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<JobDialogItem> items) {
        this.items = items;
    }

    public JobDialogItem getItem(int position) {
        return items.get(position);
    }

    public ArrayList<JobDialogItem> getItems() {
        return items;
    }

    public void setItem(int position, JobDialogItem item) {
        items.set(position, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View view, OnJobDialogClickListener listener) {
            super(view);

            text = view.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.OnItemClick(ViewHolder.this, itemView, position);
                    }
                }
            });
        }

        public void setItem(JobDialogItem item) {
            text.setText(item.getCategoryName());
        }

    }
}
