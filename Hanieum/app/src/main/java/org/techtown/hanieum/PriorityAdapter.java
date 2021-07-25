package org.techtown.hanieum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PriorityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperListener {
    ArrayList<Priority> items = new ArrayList<Priority>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == Code.ViewType.PRIORITY_NUM) { // 숫자 아이템이면
            View view = inflater.inflate(R.layout.priority_num_item, viewGroup, false);
            return new NumViewHolder(view);
        } else { // 내용 아이템이면
            View view = inflater.inflate(R.layout.priority_item, viewGroup, false);
            return new ContentsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof NumViewHolder) { // 숫자 아이템의 num 세팅
            String numStr;
            numStr = String.valueOf(items.get(position).getNum());
            ((NumViewHolder) viewHolder).num.setText(numStr);
        } else { // 내용 아이템의 contents 세팅
            ((ContentsViewHolder) viewHolder).contents.setText(items.get(position).getContents());
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

    public void addItem(Priority item) {
        items.add(item);
    }

    public void setItems(ArrayList<Priority> items) {
        this.items = items;
    }

    public Priority getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Priority item) {
        items.set(position, item);
    }

    @Override
    public boolean onMove(int formPosition, int toPosition) { // 드래그 함수
        Priority item = items.get(formPosition);
        items.remove(formPosition);
        items.add(toPosition, item);
        notifyItemMoved(formPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(int position, int direction) { // 스와이프 함수 (사용 x)
        return;
    }

    public class NumViewHolder extends RecyclerView.ViewHolder { // 숫자 아이템 ViewHolder
        TextView num;

        public NumViewHolder(View view) {
            super(view);
            num = view.findViewById(R.id.priorityNum);
        }
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder { // 내용 아이템 ViewHolder
        TextView contents;

        public ContentsViewHolder(View view) {
            super(view);
            contents = view.findViewById(R.id.priorityContents);
        }
    }
}
