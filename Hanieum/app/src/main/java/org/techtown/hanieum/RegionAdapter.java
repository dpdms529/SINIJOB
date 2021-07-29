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

public class RegionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnRegion1ItemClickListener, OnRegion2ItemClickListener {
    ArrayList<Region> items = new ArrayList<Region>();
    ArrayList<ChipList> chipList = new ArrayList<ChipList>(); // 선택된 칩을 관리하는 list
    OnRegion1ItemClickListener listener1;
    OnRegion2ItemClickListener listener2;
    private int lastSelectedPosition1 = -1; // 전에 선택한 아이템의 위치
    private int lastSelectedPosition2 = -1; // 전에 선택한 아이템의 위치

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.region_job_item, viewGroup, false);

        if (viewType == Code.ViewType.REGION1) { // 지역 분류(시/도)이면
            return new Region1ViewHolder(view, this);
        } else if (viewType == Code.ViewType.REGION2) { // 지역 분류(구/군/시)이면
            return new Region2ViewHolder(view, this);
        } else { // 지역 분류(동/읍/면)이면
            return new Region3ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Region item = items.get(position);
        if (viewHolder instanceof Region1ViewHolder) { // 지역 분류(시/도)이면
            ((Region1ViewHolder) viewHolder).setItem(item);
        } else if (viewHolder instanceof Region2ViewHolder) { // 지역 분류(구/군/시)이면
            ((Region2ViewHolder) viewHolder).setItem(item);
        } else { // 지역 분류(동/읍/면)이면
            ((Region3ViewHolder) viewHolder).setItem(item);
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

    public void addItem(Region item) {
        items.add(item);
    }

    public void setItems(ArrayList<Region> items) {
        this.items = items;
    }

    public Region getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Region item) {
        items.set(position, item);
    }

    public void setRegion1ClickListener(OnRegion1ItemClickListener listener) { this.listener1 = listener; }

    public void setRegion2ClickListener(OnRegion2ItemClickListener listener) { this.listener2 = listener; }

    @Override
    public void OnRegion1Click(Region1ViewHolder holder, View view, int position) {
        if (listener1 != null) {
            listener1.OnRegion1Click(holder, view, position);
        }
    }

    @Override
    public void OnRegion2Click(Region2ViewHolder holder, View view, int position) {
        if (listener2 != null) {
            listener2.OnRegion2Click(holder, view, position);
        }
    }

    public class Region1ViewHolder extends RecyclerView.ViewHolder {
        TextView regionText;

        public Region1ViewHolder(View view, OnRegion1ItemClickListener listener) {
            super(view);
            regionText = view.findViewById(R.id.regionJobText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        if (getLayoutPosition() != lastSelectedPosition1) { // 이미 선택한 아이템이 아니면
                            if (lastSelectedPosition1 >= 0) { // 이전에 선택한 아이템이 있으면
                                items.get(lastSelectedPosition1).setSelected(false);
                                notifyItemChanged(lastSelectedPosition1); // lastSelectedPosition 아이템 갱신
                            }
                            items.get(getLayoutPosition()).setSelected(true);
                            regionText.setBackgroundColor(Color.parseColor("#80cbc4"));

                            lastSelectedPosition1 = getLayoutPosition();
                        }

                        listener.OnRegion1Click(Region1ViewHolder.this, v, position);
                    }

                }
            });
        }

        public void setItem(Region item) {
            regionText.setText(item.getRegion1());
            if (!item.isSelected()) { // 선택된 아이템이 아니면
                regionText.setBackgroundColor(Color.WHITE);
            } else { // 선택된 아이템이면
                regionText.setBackgroundColor(Color.parseColor("#80cbc4"));
            }
        }
    }

    public class Region2ViewHolder extends RecyclerView.ViewHolder {
        TextView regionText;

        public Region2ViewHolder(View view, OnRegion2ItemClickListener listener) {
            super(view);
            regionText = view.findViewById(R.id.regionJobText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null) {
                        if (getLayoutPosition() != lastSelectedPosition2) { // 이미 선택한 아이템이 아니면
                            if (lastSelectedPosition2 >= 0) { // 이전에 선택한 아이템이 있으면
                                items.get(lastSelectedPosition2).setSelected(false);
                                notifyItemChanged(lastSelectedPosition2); // lastSelectedPosition 아이템 갱신
                            }
                            items.get(getLayoutPosition()).setSelected(true);
                            regionText.setBackgroundColor(Color.parseColor("#80cbc4"));

                            lastSelectedPosition2 = getLayoutPosition();
                        }

                        listener.OnRegion2Click(Region2ViewHolder.this, v, position);
                    }

                }
            });
        }

        public void setItem(Region item) {
            regionText.setText(item.getRegion2());
            if (!item.isSelected()) { // 선택된 아이템이 아니면
                regionText.setBackgroundColor(Color.WHITE);
            } else { // 선택된 아이템이면
                regionText.setBackgroundColor(Color.parseColor("#80cbc4"));
            }
        }
    }

    public class Region3ViewHolder extends RecyclerView.ViewHolder {
        TextView regionText;

        public Region3ViewHolder(View view) {
            super(view);
            regionText = view.findViewById(R.id.regionJobText);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    if (items.get(position).isSelected()) { // 이미 클릭된 상태이면
                        // 아이템 삭제 코드
                        for(int i=0;i<chipList.size(); i++) {
                            if(chipList.get(i).getPosition() == position)
                                chipList.remove(i);
                        }

                        items.get(position).setSelected(false);
                        regionText.setBackgroundColor(Color.WHITE);
                    } else {
                        items.get(position).setSelected(true);
                        regionText.setBackgroundColor(Color.parseColor("#80cbc4"));
                        chipList.add(new ChipList(items.get(position).getRegion3(), position));
                    }

                    loadChip();
                }
            });
        }

        public void setItem(Region item) {
            regionText.setText(item.getRegion3());
            if (!item.isSelected()) { // 선택된 아이템이 아니면
                regionText.setBackgroundColor(Color.WHITE);
            } else { // 선택된 아이템이면
                regionText.setBackgroundColor(Color.parseColor("#80cbc4"));
            }
        }

        public void loadChip() {
            RegionActivity.chipGroup.removeAllViews(); // 칩그룹 초기화
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
                        for(int i=0; i<chipList.size(); i++) {
                            if (chipList.get(i).getPosition() == position) {
                                chipList.remove(i);
                            }
                        }

                        RegionActivity.chipGroup.removeView(chip);
                        items.get(position).setSelected(false);
                        notifyItemChanged(position);
                    }
                });
                RegionActivity.chipGroup.addView(chip);
            }
        }
    }

}
