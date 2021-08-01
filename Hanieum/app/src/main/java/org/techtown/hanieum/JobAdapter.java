package org.techtown.hanieum;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static org.techtown.hanieum.SharedPreference.getArrayPref;
import static org.techtown.hanieum.SharedPreference.setArrayPref;

public class JobAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnJobItemClickListener {
    ArrayList<Job> items = new ArrayList<Job>();
    OnJobItemClickListener listener;
    public static int lastSelectedPosition1 = -1; // 전에 선택한 아이템(1차 직종)의 위치

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.region_job_item, viewGroup, false);

        if (viewType == Code.ViewType.JOB1) { // 1차 직종이면
            return new Job1ViewHolder(view, this);
        } else { // 2차 직종이면
            return new Job2ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Job item = items.get(position);
        if (viewHolder instanceof Job1ViewHolder) { // 1차 직종이면
            ((Job1ViewHolder) viewHolder).setItem(item);
        } else { // 2차 직종이면
            ((Job2ViewHolder) viewHolder).setItem(item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) { // 아이템에 정의된 viewType 사용
        return items.get(position).getViewType();
    }

    @Override
    public void OnItemClick(Job1ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.OnItemClick(holder, view, position);
        }
    }

    public void setItemClickListener(OnJobItemClickListener listener) {
        this.listener = listener;
    }

    public void addItem(Job item) {
        items.add(item);
    }

    public void setItems(ArrayList<Job> items) {
        this.items = items;
    }

    public Job getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Job item) {
        items.set(position, item);
    }

    public class Job1ViewHolder extends RecyclerView.ViewHolder {
        TextView jobText;

        public Job1ViewHolder(View view, OnJobItemClickListener listener) {
            super(view);
            jobText = view.findViewById(R.id.regionJobText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        if (position != lastSelectedPosition1) { // 이미 선택한 아이템이 아니면
                            if (lastSelectedPosition1 >= 0) { // 이전에 선택한 아이템이 있으면
                                items.get(lastSelectedPosition1).setSelected(false);
                                notifyItemChanged(lastSelectedPosition1); // lastSelectedPosition 아이템 갱신
                            }
                            items.get(position).setSelected(true);
                            notifyItemChanged(position);

                            lastSelectedPosition1 = position;

                            listener.OnItemClick(Job1ViewHolder.this, itemView, position);
                        }
                    }
                }
            });
        }

        public void setItem(Job item) {
            jobText.setText(item.getJob1());
            if (item.isSelected()) { // 선택된 아이템이면
                jobText.setBackgroundColor(Color.parseColor("#80cbc4"));
            } else { // 선택된 아이템이 아니면
                jobText.setBackgroundColor(Color.WHITE);
            }
        }
    }

    public class Job2ViewHolder extends RecyclerView.ViewHolder {
        TextView jobText;

        public Job2ViewHolder(View view) {
            super(view);
            jobText = view.findViewById(R.id.regionJobText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<ChipList> chipList = getArrayPref(itemView.getContext(), SharedPreference.JOB_LIST);
                    int position = getLayoutPosition();

                    if (items.get(position).isSelected()) { // 이미 클릭된 상태이면
                        // 아이템 삭제 코드
                        for (int i=0; i<chipList.size(); i++) {
                            if (chipList.get(i).getName().equals(items.get(position).getJob2())) {
                                chipList.remove(i);
                            }
                        }

                        items.get(position).setSelected(false);
                    } else {
                        items.get(position).setSelected(true);
                        chipList.add(new ChipList(items.get(position).getJob2(), position));
                    }
                    setArrayPref(itemView.getContext(), chipList, SharedPreference.JOB_LIST);
                    notifyItemChanged(position);
                    JobActivity.loadChip(itemView.getContext(), JobActivity.chipGroup);
                }
            });
        }

        public void setItem(Job item) {
            jobText.setText(item.getJob2());
            if (item.isSelected()) { // 선택된 아이템이면
                jobText.setBackgroundColor(Color.parseColor("#80cbc4"));
            } else { // 선택된 아이템이 아니면
                jobText.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
