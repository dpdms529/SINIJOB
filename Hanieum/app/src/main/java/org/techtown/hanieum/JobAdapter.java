package org.techtown.hanieum;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class JobAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Job> items = new ArrayList<Job>();
    ArrayList<ChipList> chipList = new ArrayList<ChipList>(); // 선택된 칩을 관리하는 list
    private int lastSelectedPosition1 = -1; // 전에 선택한 아이템(1차 직종)의 위치

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.region_job_item, viewGroup, false);

        if (viewType == Code.ViewType.JOB1) { // 1차 직종이면
            return new Job1ViewHolder(view);
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

        public Job1ViewHolder(View view) {
            super(view);
            jobText = view.findViewById(R.id.regionJobText);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLayoutPosition() != lastSelectedPosition1) { // 이미 선택한 아이템이 아니면
                        if (lastSelectedPosition1 >= 0) { // 이전에 선택한 아이템이 있으면
                            items.get(lastSelectedPosition1).setSelected(false);
                            notifyItemChanged(lastSelectedPosition1); // lastSelectedPosition 아이템 갱신
                        }
                        items.get(getLayoutPosition()).setSelected(true);
                        jobText.setBackgroundColor(Color.parseColor("#80cbc4"));

                        lastSelectedPosition1 = getLayoutPosition();
                    }
                }
            });
        }

        public void setItem(Job item) {
            jobText.setText(item.getJob1());
            if (!item.isSelected()) { // 선택된 아이템이 아니면
                jobText.setBackgroundColor(Color.WHITE);
            } else { // 선택된 아이템이면
                jobText.setBackgroundColor(Color.parseColor("#80cbc4"));
            }
        }
    }

    public class Job2ViewHolder extends RecyclerView.ViewHolder {
        TextView jobText;

        public Job2ViewHolder(View view) {
            super(view);
            jobText = view.findViewById(R.id.regionJobText);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    if (items.get(position).isSelected()) { // 이미 클릭된 상태이면
                        // 아이템 삭제 코드

                        items.get(position).setSelected(false);
                        jobText.setBackgroundColor(Color.WHITE);
                    } else {
                        items.get(position).setSelected(true);
                        jobText.setBackgroundColor(Color.parseColor("#80cbc4"));
                        chipList.add(new ChipList(items.get(position).getJob2(), position));
                    }

                    loadChip();
                }
            });
        }

        public void setItem(Job item) {
            jobText.setText(item.getJob2());
            if (!item.isSelected()) {
                jobText.setBackgroundColor(Color.WHITE);
            } else { // 선택된 아이템이면
                jobText.setBackgroundColor(Color.parseColor("#80cbc4"));
            }
        }

        public void loadChip() {
            JobActivity.chipGroup.removeAllViews(); // 칩그룹 초기화
            for (int i=0;i<chipList.size();i++) { // chipList에 있는 것을 추가
                String name = chipList.get(i).getName();
                int position = chipList.get(i).getPosition();

                Chip chip = new Chip(itemView.getContext());
                chip.setText(name);
                chip.setCloseIconResource(R.drawable.close);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(new View.OnClickListener() { // 삭제 클릭 시
                    @Override
                    public void onClick(View v) {
                        // 아이템 삭제 코드

                        JobActivity.chipGroup.removeView(chip);
                        items.get(position).setSelected(false);
                        notifyItemChanged(position);
                    }
                });
                JobActivity.chipGroup.addView(chip);
            }
        }
    }
}
