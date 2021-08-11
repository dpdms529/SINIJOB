package org.techtown.hanieum;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    Button changeButton; // 조건 변경 화면으로 이동하는 버튼
    ImageButton searchButton; // 검색 버튼
    TextView itemNum;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recommendView);
        changeButton = view.findViewById(R.id.changeButton);
        searchButton = view.findViewById(R.id.searchButton);
        itemNum = view.findViewById(R.id.itemNum);

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

        searchButton.setOnClickListener(this);

        loadListData();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            Toast.makeText(getContext(), "검색", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadListData() {
        ArrayList<Recommendation> items = new ArrayList<>();

        itemNum.setText("20");

        for (int i=0; i<20; i++) {
            items.add(new Recommendation("DdA0402108062034", "(주)한경", "상가 관리소장 모집", "월", "105", 1100.0, false));
        }

        adapter.setItems(items);
    }
}