package org.techtown.hanieum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Search> items = new ArrayList<Search>();
    SharedPreference pref;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.search_item, viewGroup, false);

        if (viewType == Code.ViewType.JOB_SEARCH) { // 직업 검색이면
            pref = JobSearchActivity.pref;
            return new JobViewHolder(view);
        } else { // 지역 검색이면
            pref = RegionSearchActivity.pref;
            return new RegionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Search item = items.get(position);

        if (viewHolder instanceof JobViewHolder) { // 직업 검색이면
            ((JobViewHolder) viewHolder).setItem(item);
        } else { // 지역 검색이면
            ((RegionViewHolder) viewHolder).setItem(item);
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

    public void addItem(Search item) {
        items.add(item);
    }

    public void setItems(ArrayList<Search> items) {
        this.items = items;
    }

    public Search getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Search item) {
        items.set(position, item);
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public JobViewHolder(View view) {
            super(view);

            checkBox = view.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<ChipList> chipList = pref.getArrayPref(SharedPreference.JOB_TMP);
                    int position = getLayoutPosition();

                    if (items.get(position).isChecked()) { // 이미 클릭된 상태이면
                        // 아이템 삭제 코드
                        for (int i=0; i<chipList.size(); i++) {
                            if (chipList.get(i).getName().equals(items.get(position).getTitle())) {
                                chipList.remove(i);
                            }
                        }

                        items.get(position).setChecked(false);
                    } else {
                        for (int i=0; i<chipList.size(); i++) {
                            // 선택한 아이템이 1차 직종 전체에 해당하면
                            if (items.get(position).getCode().contains(chipList.get(i).getCode())) {
                                chipList.remove(i);
                                break;
                            }
                        }

                        items.get(position).setChecked(true);
                        chipList.add(new ChipList(items.get(position).getTitle(),items.get(position).getCode(), position));
                    }
                    pref.setArrayPref(chipList, SharedPreference.JOB_TMP);
                    notifyItemChanged(position);
                    JobSearchActivity.loadChip(itemView.getContext(), JobSearchActivity.chipGroup);
                }
            });
        }

        public void setItem(Search item) {
            checkBox.setText(item.getTitle());
            if (item.isChecked()) { // 체크 상태이면
                checkBox.setChecked(true);
            } else { // 체크 상태가 아니면
                checkBox.setChecked(false);
            }
        }
    }

    public class RegionViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public RegionViewHolder(View view) {
            super(view);

            checkBox = view.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getLayoutPosition();
                    if (isChecked) { // 체크 상태이면
                        items.get(position).setChecked(true);
//                        chipList.add(new ChipList(items.get(position).getTitle(), position));
                    } else { // 체크 상태가 아니면
                        items.get(position).setChecked(false);

                        // 아이템 삭제 코드
                    }
//                    loadChip();
                }
            });
        }

        public void setItem(Search item) {
            checkBox.setText(item.getTitle());
            if (item.isChecked()) { // 체크 상태이면
                checkBox.setChecked(true);
            } else { // 체크 상태가 아니면
                checkBox.setChecked(false);
            }
        }
    }
}
