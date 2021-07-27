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
import android.widget.Toast;

public class RecommendFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    Button changeButton; // 조건 변경 화면으로 이동하는 버튼
    ImageButton searchButton; // 검색 버튼
    ImageButton menuButton; // 메뉴 버튼

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        recyclerView = view.findViewById(R.id.recommendView);
        changeButton = view.findViewById(R.id.changeButton);
        searchButton = view.findViewById(R.id.searchButton);
        menuButton = view.findViewById(R.id.menuButton);

        // 리사이클러뷰와 어댑터 연결
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1)); // 구분선
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecommendAdapter();
        recyclerView.setAdapter(adapter);

        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));
        adapter.addItem(new Recommendation("무진장흑돼지푸드",
                "사무경리 여직원/생산직 구함", "자동차", "25분", false));

        changeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == changeButton) {
            Intent intent = new Intent(this.getContext(), FilteringActivity.class);
            startActivity(intent);
        } else if (v == searchButton) {
            Toast.makeText(v.getContext(), "검색 버튼 눌림", Toast.LENGTH_LONG).show();
        } else if (v == menuButton) {
            Toast.makeText(v.getContext(), "메뉴 버튼 눌림", Toast.LENGTH_LONG).show();
        }
    }
}