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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        ArrayList<Recommendation> items = new ArrayList<>();

        String list = getResources().getString(R.string.serverIP)+"recruit.php";
        URLConnector task = new URLConnector(list);

        task.start();

        try {
            task.join();
            System.out.println("Waiting...for result");
        }
        catch(InterruptedException e) {

        }

        String result = task.getResult();

        try {
            JSONObject jsonObject = new JSONObject(result);
            String num = jsonObject.getString("rownum");
            itemNum.setText(num);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i=0; i<jsonArray.length(); i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String id = jsonObject1.getString("recruit_id");
                String title = jsonObject1.getString("title");
                String organization = jsonObject1.getString("organization");

                items.add(new Recommendation(id, organization, title, "자동차",
                        "25분", false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setItems(items);
    }
}