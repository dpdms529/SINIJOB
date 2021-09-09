package org.techtown.hanieum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CertifiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Certificate> items = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.certifi_item, viewGroup, false);
        return new CertifiAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Certificate item = items.get(position);
        ((CertifiAdapter.ViewHolder) viewHolder).setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Certificate item) {
        items.add(item);
    }

    public void setItems(ArrayList<Certificate> items) {
        this.items = items;
    }

    public Certificate getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Certificate item) {
        items.set(position, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        public void setItem(Certificate item) {
        }
    }
}
