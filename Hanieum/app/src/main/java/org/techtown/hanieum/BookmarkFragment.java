package org.techtown.hanieum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.techtown.hanieum.db.AppDatabase;

public class BookmarkFragment extends Fragment {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    TextView itemNum;

    AppDatabase db;

    Context context;

    public BookmarkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        context = getContext();
        recyclerView = view.findViewById(R.id.recommendView);
        itemNum = view.findViewById(R.id.itemNum);

        db = AppDatabase.getInstance(this.getContext());

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1)); // 구분선
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setItemClickListener(new OnRecoItemClickListener() {
            @Override
            public void OnItemClick(RecommendAdapter.ViewHolder holder, View view, int position) {
                Recommendation item = adapter.getItem(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });

        return view;
    }
}