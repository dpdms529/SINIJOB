package org.techtown.hanieum;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CertifiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Certificate> items = new ArrayList<>();
    Context context;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.certifi_item, viewGroup, false);
        context = inflater.getContext();
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

    public ArrayList<Certificate> getItmes(){
        return items;
    }

    public Certificate getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Certificate item) {
        items.set(position, item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView delete;
        TextView certificate;

        public ViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.delete);
            certificate = view.findViewById(R.id.certificate);

            delete.setOnClickListener(this);
            certificate.setOnClickListener(this);
        }

        public void setItem(Certificate item) {
            if(item.getCertifi() == null){
                certificate.setHint("자격증을 선택해주세요");
            }else {
                certificate.setText(item.getCertifi());
            }
        }

        @Override
        public void onClick(View v) {
            if(v == delete){
                AlertDialog.Builder msgBuilder = new AlertDialog.Builder(context, R.style.MaterialAlertDialog_OK_color)
                        .setTitle("삭제").setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 아이템 삭제
                                items.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                AlertDialog msgDlg = msgBuilder.create();
                msgDlg.show();
            }else if(v == certificate){
                JobDialog dialog = new JobDialog(context, items.get(getAdapterPosition()));
                Log.d("TAG", "onClick: " + items.get(getAdapterPosition()).getClass());
                dialog.show();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface d) {
                        certificate.setText(items.get(getAdapterPosition()).getCertifi());
                        Log.d("TAG", "onClick: " + items.get(getAdapterPosition()).getCertifiCode() + items.get(getAdapterPosition()).getCertifi());
                    }
                });
            }

        }
    }
}
