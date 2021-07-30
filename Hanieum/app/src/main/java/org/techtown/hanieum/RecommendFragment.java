package org.techtown.hanieum;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.hanieum.db.AppDatabase;
import org.techtown.hanieum.db.entity.Recruit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RecommendFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView; // 추천 목록 리사이클러뷰
    RecommendAdapter adapter; // 추천 목록 어댑터
    Button changeButton; // 조건 변경 화면으로 이동하는 버튼
    ImageButton searchButton; // 검색 버튼
    ImageButton menuButton; // 메뉴 버튼
    TextView itemNum;

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

        changeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);

        loadListData();

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

    private void loadListData() { // 항목을 로드하는 함수
        // 항목 로드
        AppDatabase db = AppDatabase.getInstance(this.getContext());
        List<Recruit> rows = db.RecruitDao().getAll();
        Log.d("dbdb", String.valueOf(rows.get(0).recruit_id));

        ArrayList<Recommendation> items = new ArrayList<>();

        itemNum.setText(String.valueOf(rows.size()));

        for (int i=0; i<rows.size(); i++) {
            Recruit row = rows.get(i);
            String salaryType = new String();
            switch (row.salary_type_code) {     // 급여 타입에 알맞은 단어
                case "H":
                    salaryType = "시";
                    break;
                case "D":
                    salaryType = "일";
                    break;
                case "M":
                    salaryType = "월";
                    break;
                case "Y":
                    salaryType = "연";
                    break;
            }
            String sal = new String();
            String[] tmp = row.salary.split(" ~ ");
            if (tmp.length == 1 || tmp[0].equals(tmp[1])) {
                String[] tmp2 = tmp[0].split("만원|원");
                sal = tmp2[0];
            } else {
                String[] tmp2 = tmp[0].split("만원|원");
                String[] tmp3 = tmp[1].split("만원|원");
                sal = tmp2[0] + " ~ " + tmp3[0];
            }
            DistanceCalculator distance =  new DistanceCalculator("127.12934", "35.84688", row.x_coordinate, row.y_coordinate);
            Double dist = distance.getStraightDist();   // 직선거리 구하는 함수
            items.add(new Recommendation(row.recruit_id, row.organization, row.recruit_title, salaryType, sal, dist, false));
        }
        Collections.sort(items);    // 거리순으로 정렬
        adapter.setItems(items);
    }
}